package org.snomed.reasoner.protege;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.elk.protege.ProtegeReasonerFactory;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.snomed.reasoner.SnomedAbsentOWLReasonerFactory;

public class SnomedAbsentProtegeReasonerFactory extends ProtegeReasonerFactory {

	protected final OWLReasonerFactory factory;

	public SnomedAbsentProtegeReasonerFactory() {
		super();
		ElkReasonerFactory elkReasonerFactory = (ElkReasonerFactory) super.getReasonerFactory();
		factory = new SnomedAbsentOWLReasonerFactory(elkReasonerFactory);
	}

	@Override
	public OWLReasonerFactory getReasonerFactory() {
		return factory;
	}
}
