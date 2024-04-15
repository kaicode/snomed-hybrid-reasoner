package org.snomed.reasoner;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import javax.annotation.Nonnull;

public class SnomedAbsentOWLReasonerFactory implements OWLReasonerFactory {

	public static final String KNOWN_ABSENT = "410516002";
	private final ElkReasonerFactory elkReasonerFactory;

	public SnomedAbsentOWLReasonerFactory() {
		elkReasonerFactory = new ElkReasonerFactory();
	}

	public SnomedAbsentOWLReasonerFactory(ElkReasonerFactory elkReasonerFactory) {
		this.elkReasonerFactory = elkReasonerFactory;
	}

	@Nonnull
	@Override
	public String getReasonerName() {
		return "SnomedAbsentOWLReasoner";
	}

	@Nonnull
	@Override
	public OWLReasoner createReasoner(@Nonnull OWLOntology owlOntology, @Nonnull OWLReasonerConfiguration owlReasonerConfiguration) {
		OWLReasoner baseElkReasoner = elkReasonerFactory.createReasoner(owlOntology, owlReasonerConfiguration);
		return createSnomedAbsentOwlReasoner(owlOntology, baseElkReasoner);
	}

	@Nonnull
	@Override
	public OWLReasoner createReasoner(@Nonnull OWLOntology owlOntology) {
		OWLReasoner baseElkReasoner = elkReasonerFactory.createReasoner(owlOntology);
		return createSnomedAbsentOwlReasoner(owlOntology, baseElkReasoner);
	}

	private static SnomedAbsentOwlReasoner createSnomedAbsentOwlReasoner(OWLOntology owlOntology, OWLReasoner baseElkReasoner) {
		return new SnomedAbsentOwlReasoner(owlOntology, baseElkReasoner, KNOWN_ABSENT);
	}

	@Nonnull
	@Override
	public OWLReasoner createNonBufferingReasoner(@Nonnull OWLOntology owlOntology) {
		throw new UnsupportedOperationException();
	}

	@Nonnull
	@Override
	public OWLReasoner createNonBufferingReasoner(@Nonnull OWLOntology owlOntology, @Nonnull OWLReasonerConfiguration owlReasonerConfiguration) {
		throw new UnsupportedOperationException();
	}
}
