# SNOMED-CT Absent Hybrid-Reasoner - EXPERIMENTAL

## Status
An unofficial **experimental** OWL Reasoner to support discussions and content investigations within the Context Management working group.

## What does it do?
It changes the subsumption behaviour between concepts within the "Situation with explicit context (situation)" hierarchy 
that use the "Finding context" is "Known absent".

### Why?
In the current SNOMED CT representation the negation aspect of the "Known absent" attribute is not part of the subsumption evaluation. 
This results in a hierarchy with inconsistent semantics and limited value.

The purpose of this reasoner is to correct the subsumption between these concepts so that the project group can evaluate and discuss the output.

## How does it work?
This is a hybrid reasoner. The Absent reasoner wraps the existing ELK reasoner. 

1. All the heavy classification work is delegated to ELK because that is very complex and highly optimised. The set of absent concepts, 
those with "Finding context" "Known absent", is captured when ELK completes.
2. As the subclass and superclass relationships are extracted from the inferred hierarchy of the reasoner additional logic is applied 
to absent concepts. Subclass and superclass relationships are effectively reversed for the concepts within this set.  

## How can it be used?
There are two modules:
- An OWL Reasoner
    - Can be used with the SNOMED OWL Toolkit to create the NNF
- A Protégé Plugin
    - Can be used to experiment with ontologies directly in the Protégé desktop application

### To use the OWL Reasoner
To use the OWL Reasoner with the SNOMED OWL Toolkit include the snomed-absent-reasoner jar on the classpath.

Pre-built jar files are available under Assets on the [releases page](https://github.com/kaicode/snomed-hybrid-reasoner/releases).

Then set the `reasonerFactoryClassName` parameter to `org.snomed.reasoner.SnomedAbsentOWLReasonerFactory`.

### To use the Protégé plugin
Copy the snomed-absent-reasoner-protege-plugin to the Protégé plugins folder and restart Protégé.

Pre-built jar files are available under Assets on the [releases page](https://github.com/kaicode/snomed-hybrid-reasoner/releases).

On MacOS the plugins folder is `/Applications/Protégé.app/Contents/plugins/`.
