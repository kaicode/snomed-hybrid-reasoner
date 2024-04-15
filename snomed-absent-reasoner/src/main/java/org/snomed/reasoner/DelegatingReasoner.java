package org.snomed.reasoner;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.Version;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

/**
 * This class implements all methods of the OWLReasoner interface with empty methods that do nothing.
 */
public class DelegatingReasoner implements OWLReasoner {

	private final OWLReasoner delegate;

	public DelegatingReasoner(OWLReasoner delegate) {
		this.delegate = delegate;
	}

	@Nonnull
	@Override
	public String getReasonerName() {
		return delegate.getReasonerName();
	}

	@Nonnull
	@Override
	public Version getReasonerVersion() {
		return delegate.getReasonerVersion();
	}

	@Nonnull
	@Override
	public BufferingMode getBufferingMode() {
		return delegate.getBufferingMode();
	}

	@Override
	public void flush() {
		delegate.flush();
	}

	@Nonnull
	@Override
	public List<OWLOntologyChange> getPendingChanges() {
		return delegate.getPendingChanges();
	}

	@Nonnull
	@Override
	public Set<OWLAxiom> getPendingAxiomAdditions() {
		return delegate.getPendingAxiomAdditions();
	}

	@Nonnull
	@Override
	public Set<OWLAxiom> getPendingAxiomRemovals() {
		return delegate.getPendingAxiomRemovals();
	}

	@Nonnull
	@Override
	public OWLOntology getRootOntology() {
		return delegate.getRootOntology();
	}

	@Override
	public void interrupt() {
		delegate.interrupt();
	}

	@Override
	public void precomputeInferences(@Nonnull InferenceType... inferenceTypes) {
		delegate.precomputeInferences(inferenceTypes);
	}

	@Override
	public boolean isPrecomputed(@Nonnull InferenceType inferenceType) {
		return delegate.isPrecomputed(inferenceType);
	}

	@Nonnull
	@Override
	public Set<InferenceType> getPrecomputableInferenceTypes() {
		return delegate.getPrecomputableInferenceTypes();
	}

	@Override
	public boolean isConsistent() {
		return delegate.isConsistent();
	}

	@Override
	public boolean isSatisfiable(@Nonnull OWLClassExpression classExpression) {
		return delegate.isSatisfiable(classExpression);
	}

	@Nonnull
	@Override
	public Node<OWLClass> getUnsatisfiableClasses() {
		return delegate.getUnsatisfiableClasses();
	}

	@Override
	public boolean isEntailed(@Nonnull OWLAxiom axiom) {
		return delegate.isEntailed(axiom);
	}

	@Override
	public boolean isEntailed(@Nonnull Set<? extends OWLAxiom> axioms) {
		return delegate.isEntailed(axioms);
	}

	@Override
	public boolean isEntailmentCheckingSupported(@Nonnull AxiomType<?> axiomType) {
		return delegate.isEntailmentCheckingSupported(axiomType);
	}

	@Nonnull
	@Override
	public Node<OWLClass> getTopClassNode() {
		return delegate.getTopClassNode();
	}

	@Nonnull
	@Override
	public Node<OWLClass> getBottomClassNode() {
		return delegate.getBottomClassNode();
	}

	@Nonnull
	@Override
	public NodeSet<OWLClass> getSubClasses(@Nonnull OWLClassExpression ce, boolean direct) {
		return delegate.getSubClasses(ce, direct);
	}

	@Nonnull
	@Override
	public NodeSet<OWLClass> getSuperClasses(@Nonnull OWLClassExpression ce, boolean direct) {
		return delegate.getSuperClasses(ce, direct);
	}

	@Nonnull
	@Override
	public Node<OWLClass> getEquivalentClasses(@Nonnull OWLClassExpression ce) {
		return delegate.getEquivalentClasses(ce);
	}

	@Nonnull
	@Override
	public NodeSet<OWLClass> getDisjointClasses(@Nonnull OWLClassExpression ce) {
		return delegate.getDisjointClasses(ce);
	}

	@Nonnull
	@Override
	public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
		return delegate.getTopObjectPropertyNode();
	}

	@Nonnull
	@Override
	public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
		return delegate.getBottomObjectPropertyNode();
	}

	@Nonnull
	@Override
	public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(@Nonnull OWLObjectPropertyExpression pe, boolean direct) {
		return delegate.getSubObjectProperties(pe, direct);
	}

	@Nonnull
	@Override
	public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(@Nonnull OWLObjectPropertyExpression pe, boolean direct) {
		return delegate.getSuperObjectProperties(pe, direct);
	}

	@Nonnull
	@Override
	public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(@Nonnull OWLObjectPropertyExpression pe) {
		return delegate.getEquivalentObjectProperties(pe);
	}

	@Nonnull
	@Override
	public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(@Nonnull OWLObjectPropertyExpression pe) {
		return delegate.getDisjointObjectProperties(pe);
	}

	@Nonnull
	@Override
	public Node<OWLObjectPropertyExpression> getInverseObjectProperties(@Nonnull OWLObjectPropertyExpression pe) {
		return delegate.getInverseObjectProperties(pe);
	}

	@Nonnull
	@Override
	public NodeSet<OWLClass> getObjectPropertyDomains(@Nonnull OWLObjectPropertyExpression pe, boolean direct) {
		return delegate.getObjectPropertyDomains(pe, direct);
	}

	@Nonnull
	@Override
	public NodeSet<OWLClass> getObjectPropertyRanges(@Nonnull OWLObjectPropertyExpression pe, boolean direct) {
		return delegate.getObjectPropertyRanges(pe, direct);
	}

	@Nonnull
	@Override
	public Node<OWLDataProperty> getTopDataPropertyNode() {
		return delegate.getTopDataPropertyNode();
	}

	@Nonnull
	@Override
	public Node<OWLDataProperty> getBottomDataPropertyNode() {
		return delegate.getBottomDataPropertyNode();
	}

	@Nonnull
	@Override
	public NodeSet<OWLDataProperty> getSubDataProperties(@Nonnull OWLDataProperty pe, boolean direct) {
		return delegate.getSubDataProperties(pe, direct);
	}

	@Nonnull
	@Override
	public NodeSet<OWLDataProperty> getSuperDataProperties(@Nonnull OWLDataProperty pe, boolean direct) {
		return delegate.getSuperDataProperties(pe, direct);
	}

	@Nonnull
	@Override
	public Node<OWLDataProperty> getEquivalentDataProperties(@Nonnull OWLDataProperty pe) {
		return delegate.getEquivalentDataProperties(pe);
	}

	@Nonnull
	@Override
	public NodeSet<OWLDataProperty> getDisjointDataProperties(@Nonnull OWLDataPropertyExpression pe) {
		return delegate.getDisjointDataProperties(pe);
	}

	@Nonnull
	@Override
	public NodeSet<OWLClass> getDataPropertyDomains(@Nonnull OWLDataProperty pe, boolean direct) {
		return delegate.getDataPropertyDomains(pe, direct);
	}

	@Nonnull
	@Override
	public NodeSet<OWLClass> getTypes(@Nonnull OWLNamedIndividual ind, boolean direct) {
		return delegate.getTypes(ind, direct);
	}

	@Nonnull
	@Override
	public NodeSet<OWLNamedIndividual> getInstances(@Nonnull OWLClassExpression ce, boolean direct) {
		return delegate.getInstances(ce, direct);
	}

	@Nonnull
	@Override
	public NodeSet<OWLNamedIndividual> getObjectPropertyValues(@Nonnull OWLNamedIndividual ind, @Nonnull OWLObjectPropertyExpression pe) {
		return delegate.getObjectPropertyValues(ind, pe);
	}

	@Nonnull
	@Override
	public Set<OWLLiteral> getDataPropertyValues(@Nonnull OWLNamedIndividual ind, @Nonnull OWLDataProperty pe) {
		return delegate.getDataPropertyValues(ind, pe);
	}

	@Nonnull
	@Override
	public Node<OWLNamedIndividual> getSameIndividuals(@Nonnull OWLNamedIndividual ind) {
		return delegate.getSameIndividuals(ind);
	}

	@Nonnull
	@Override
	public NodeSet<OWLNamedIndividual> getDifferentIndividuals(@Nonnull OWLNamedIndividual ind) {
		return delegate.getDifferentIndividuals(ind);
	}

	@Override
	public long getTimeOut() {
		return delegate.getTimeOut();
	}

	@Nonnull
	@Override
	public FreshEntityPolicy getFreshEntityPolicy() {
		return delegate.getFreshEntityPolicy();
	}

	@Nonnull
	@Override
	public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
		return delegate.getIndividualNodeSetPolicy();
	}

	@Override
	public void dispose() {
		delegate.dispose();
	}
}
