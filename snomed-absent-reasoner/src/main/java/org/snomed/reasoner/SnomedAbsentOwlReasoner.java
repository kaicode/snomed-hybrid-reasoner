package org.snomed.reasoner;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static org.semanticweb.owlapi.reasoner.InferenceType.CLASS_HIERARCHY;
import static org.snomed.reasoner.OntologyHelper.SNOMED_CORE_COMPONENTS_URI;

public class SnomedAbsentOwlReasoner extends DelegatingReasoner {

	private final OWLOntology owlOntology;
	private final OWLReasoner baseReasoner;
	private final String absentPropertyValue;
	private Set<OWLClass> elkAbsentTopClasses;
	private Set<OWLClass> allAbsentClasses;
	private Set<OWLClass> elkAbsentLeafClasses;
	private Map<Node<OWLClass>, Set<Node<OWLClass>>> elkTopNodeToLeafNodesMap;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public SnomedAbsentOwlReasoner(OWLOntology owlOntology, OWLReasoner baseReasoner, String absentPropertyValue) {
		super(baseReasoner);
		this.owlOntology = owlOntology;
		this.baseReasoner = baseReasoner;
		this.absentPropertyValue = absentPropertyValue;
	}

	@Nonnull
	@Override
	public String getReasonerName() {
		return "SNOMED Absent Reasoner";
	}

	@Override
	public void precomputeInferences(@Nonnull InferenceType... inferenceTypes) {
		assertInferenceType(inferenceTypes);

		Set<Node<OWLClass>> negatedTopNodes = getNegatedTopNodes();
		elkAbsentTopClasses = negatedTopNodes.stream().map(Node::getRepresentativeElement).collect(Collectors.toSet());
		logger.info("Found {} absent top classes: {}", elkAbsentTopClasses.size(), elkAbsentTopClasses.stream().map(OWLClass::getIRI).collect(Collectors.toList()));

		elkTopNodeToLeafNodesMap = getTopNodeToLeafNodesMap(negatedTopNodes);
		elkAbsentLeafClasses = elkTopNodeToLeafNodesMap.values().stream()
				.flatMap(Collection::stream).map(Node::getRepresentativeElement).collect(Collectors.toSet());

		allAbsentClasses = getAllSubclasses(elkAbsentTopClasses);
		allAbsentClasses.addAll(elkAbsentTopClasses);
	}

	/*

	Elk hierarchy:
	 A
	  B-
	   C-
	    D-

	Absent reasoner hierarchy:
	 A
	  D-
	   C-
	    B-

	Subclass of A -> D
	Subclass of D -> C
	Subclass of C -> B
	Subclass of B -> Nothing
	 */

	@Nonnull
	@Override
	public NodeSet<OWLClass> getSubClasses(OWLClassExpression classExpression, boolean direct) {
		assertDirectParameterIsTrue(direct);
		OWLClass owlClass = classExpression.asOWLClass();

		if (!allAbsentClasses.contains(owlClass)) {
			// Class is normal, not absent
			// Fetch subclasses from base reasoner
			NodeSet<OWLClass> subClasses = baseReasoner.getSubClasses(classExpression, true);

			// Replace any 'absent' subclasses with 'absent' leaf nodes
			subClasses = replaceAbsentTopNodesWithLeafNodes(subClasses);

			return subClasses;
		} else if (elkAbsentTopClasses.contains(owlClass)) {
			// Class is absent root node
			// Present node as leaf by returning empty set of subclasses
			return new OWLClassNodeSet();
		} else {
			// Class is absent middle node
			// Fetch absent super-classes rather than subclasses, reverse direction
			// Remove any non-absent classes
			Set<Node<OWLClass>> absentSuperClasses = baseReasoner.getSuperClasses(classExpression, true).getNodes().stream()
					.filter(node -> allAbsentClasses.contains(node.getRepresentativeElement()))
					.collect(Collectors.toSet());
			return new OWLClassNodeSet(absentSuperClasses);
		}
	}

	@Nonnull
	@Override
	public NodeSet<OWLClass> getSuperClasses(OWLClassExpression classExpression, boolean direct) {
		assertDirectParameterIsTrue(direct);

		OWLClass owlClass = classExpression.asOWLClass();
		if (allAbsentClasses.contains(owlClass)) {
			// Absent class
			if (!elkAbsentLeafClasses.contains(owlClass)) {
				// Not Elk leaf class
				// Fetch subclasses rather than superclasses, reverse direction
				return baseReasoner.getSubClasses(owlClass, true);// true means 'direct' subclasses only, not transitive
			} else {
				// Elk absent leaf class
				// Return ancestors of elk top level absent node instead
				Set<Node<OWLClass>> superClasses = new HashSet<>();
				for (Node<OWLClass> topNegatedNode : elkTopNodeToLeafNodesMap.keySet()) {
					Set<OWLClass> leafNegatedNodes = elkTopNodeToLeafNodesMap.get(topNegatedNode).stream()
							.map(Node::getRepresentativeElement).collect(Collectors.toSet());
					if (leafNegatedNodes.contains(owlClass)) {
						superClasses.addAll(baseReasoner.getSuperClasses(topNegatedNode.getRepresentativeElement(), true).getNodes());
					}
				}
				return new OWLClassNodeSet(superClasses);
			}
		} else {
			// Not absent class
			return baseReasoner.getSuperClasses(classExpression, true);
		}
	}

	@Nonnull
	@Override
	public Set<InferenceType> getPrecomputableInferenceTypes() {
		return Collections.singleton(CLASS_HIERARCHY);
	}

	private NodeSet<OWLClass> replaceAbsentTopNodesWithLeafNodes(NodeSet<OWLClass> subClasses) {
		Set<Node<OWLClass>> subClassesToRemove = new HashSet<>();
		Set<Node<OWLClass>> subClassesToAdd = new HashSet<>();
		for (Node<OWLClass> subClass : subClasses) {
			// if subclass is 'absent'
			if (elkAbsentTopClasses.contains(subClass.getRepresentativeElement())) {
				// Replace with leaves
				subClassesToRemove.add(subClass);
				subClassesToAdd.addAll(elkTopNodeToLeafNodesMap.get(subClass));
			}
		}
		// Some 'absent' subclasses
		if (!subClassesToRemove.isEmpty()) {
			// Create new subclass set with original absent classes replaced by the absent leaves
			Set<Node<OWLClass>> newSubClassSet = new HashSet<>(subClasses.getNodes());
			newSubClassSet.removeAll(subClassesToRemove);
			newSubClassSet.addAll(subClassesToAdd);
			subClasses = new OWLClassNodeSet(newSubClassSet);
		}
		return subClasses;
	}

	private void assertDirectParameterIsTrue(boolean direct) {
		if (!direct) {
			throw new UnsupportedOperationException("This reasoner only supports fetching direct subclasses and superclasses.");
		}
	}

	private Set<OWLClass> getAllSubclasses(Set<OWLClass> classesToIterate) {
		Set<OWLClass> allSubclasses = new HashSet<>();
		for (OWLClass owlClass : classesToIterate) {
			boolean direct = false;// false = all descendants, not just children
			Set<OWLClass> subClasses = baseReasoner.getSubClasses(owlClass, direct).getFlattened();
			for (OWLClass subClass : subClasses) {
				if (OntologyHelper.isConceptClass(subClass)) {
					allSubclasses.add(subClass);
				}
			}
		}
		return allSubclasses;
	}

	private Map<Node<OWLClass>, Set<Node<OWLClass>>> getTopNodeToLeafNodesMap(Set<Node<OWLClass>> topNodes) {
		Map<Node<OWLClass>, Set<Node<OWLClass>>> map = new HashMap<>();
		for (Node<OWLClass> topNode : topNodes) {
			map.put(topNode, getLeaves(topNode, new HashSet<>()));
		}
		return map;
	}

	private Set<Node<OWLClass>> getLeaves(Node<OWLClass> node, Set<Node<OWLClass>> leaves) {
		if (node.isBottomNode()) {
			leaves.add(node);
		} else {
			NodeSet<OWLClass> subClasses = baseReasoner.getSubClasses(node.getRepresentativeElement(), true);
			Set<Node<OWLClass>> subClassNodes = subClasses.getNodes();
			if (subClassNodes.size() == 1 && subClassNodes.iterator().next().isBottomNode()) {
				leaves.add(node);
			} else {
				for (Node<OWLClass> subClass : subClasses) {
					getLeaves(subClass, leaves);
				}
			}
		}
		return leaves;
	}

	private Set<Node<OWLClass>> getNegatedTopNodes() {
		Set<Node<OWLClass>> negatedClasses = new HashSet<>();
		Node<OWLClass> topClassNode = baseReasoner.getTopClassNode();
		NodeSet<OWLClass> subClasses = baseReasoner.getSubClasses(topClassNode.getRepresentativeElement(), true);
		Set<Long> processedConcepts = new HashSet<>();
		processedConcepts.add(Long.parseLong(absentPropertyValue));// Don't process this class
		collectNegatedTopNodes(subClasses, negatedClasses, processedConcepts);
		return negatedClasses;
	}

	private void collectNegatedTopNodes(NodeSet<OWLClass> nodes, Set<Node<OWLClass>> negatedNodes, Set<Long> processedConcepts) {
		for (Node<OWLClass> node : nodes) {
			if (OntologyHelper.isConceptClass(node)) {
				Long conceptId = OntologyHelper.getConceptId(node);
				if (processedConcepts.add(conceptId)) {
					OWLClass owlClass = node.getRepresentativeElement();
					Set<OWLClassAxiom> axioms = owlOntology.getAxioms(owlClass, Imports.EXCLUDED);
					boolean negatedPropertyInAxiom = false;
					for (OWLClassAxiom axiom : axioms) {
						if (isNegatedPropertyInAxiom(axiom)) {
							negatedPropertyInAxiom = true;
							negatedNodes.add(node);
						}
					}
					if (negatedPropertyInAxiom) {
						// Negated class, do not traverse further
						continue;
					}
					// Not a negated class, traverse further into subclasses
					NodeSet<OWLClass> subClasses = baseReasoner.getSubClasses(owlClass, true);
					collectNegatedTopNodes(subClasses, negatedNodes, processedConcepts);
				}
			}
		}
	}

	private boolean isNegatedPropertyInAxiom(OWLClassAxiom axiom) {
		if (axiom instanceof OWLSubClassOfAxiom) {
			return isNegatedPropertyInClassExpression(((OWLSubClassOfAxiom) axiom).getSubClass());
		} else if (axiom instanceof OWLEquivalentClassesAxiom) {
			for (OWLClassExpression classExpression : ((OWLEquivalentClassesAxiom) axiom).getClassExpressions()) {
				if (isNegatedPropertyInClassExpression(classExpression)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isNegatedPropertyInClassExpression(OWLClassExpression classExpression) {
		if (classExpression instanceof OWLObjectIntersectionOf) {
			for (OWLClassExpression operand : ((OWLObjectIntersectionOf) classExpression).getOperands()) {
				if (isNegatedPropertyInClassExpression(operand)) {
					return true;
				}
			}
		} else if (classExpression instanceof OWLObjectSomeValuesFrom) {
			return isNegatedPropertyInClassExpression(((OWLObjectSomeValuesFrom) classExpression).getFiller());
		} else if (!classExpression.isAnonymous()) {
			String uri = classExpression.asOWLClass().getIRI().toString();
			String negatedPropertyUri = SNOMED_CORE_COMPONENTS_URI + absentPropertyValue;
			return uri.equals(negatedPropertyUri);
		}
		return false;
	}

	private static void assertInferenceType(InferenceType[] inferenceTypes) {
		if (inferenceTypes.length != 1 || inferenceTypes[0] != CLASS_HIERARCHY) {
			throw new UnsupportedOperationException("This reasoner only supports CLASS_HIERARCHY inference.");
		}
	}
}
