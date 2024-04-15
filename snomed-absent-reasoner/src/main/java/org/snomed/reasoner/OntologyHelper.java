package org.snomed.reasoner;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.reasoner.Node;

public class OntologyHelper {

	public static final String SNOMED_CORE_COMPONENTS_URI = "http://snomed.info/id/";

	public static boolean isConceptClass(final OWLClass owlClass) {
		return owlClass.getIRI().toString().startsWith(SNOMED_CORE_COMPONENTS_URI);
	}

	public static long getConceptId(final OWLNamedObject owlNamedObject) {
		return Long.parseLong(owlNamedObject.getIRI().toString().substring(SNOMED_CORE_COMPONENTS_URI.length()));
	}

	public static boolean isConceptClass(Node<OWLClass> currentNode) {
		for (OWLClass owlClass : currentNode) {
			if (isConceptClass(owlClass)) {
				return true;
			}
		}
		return false;
	}

	public static Long getConceptId(Node<OWLClass> currentNode) {
		for (OWLClass owlClass : currentNode) {
			if (isConceptClass(owlClass)) {
				return getConceptId(owlClass);
			}
		}
		return null;
	}
}
