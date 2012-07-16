JADE-PKI
========

Author: Amadeusz Żołnowski
Mentor: Piotr Szpryngier, PhD MEng
Organisation: Gdańsk University of Technology


Introduction
------------

The purpose of the JADE-PKI add-on is to introduce a public key infrastructure
into JADE. The add-on provides security for two areas: agent messaging and
agent mobility.  The JADE-PKI might take inspiration from the JADE-Security [1]
in some parts of agent messaging area, therefore the project has the same
license.

[1] https://avalon.cselt.it/svn/jade_add-ons/trunk/security/


Agent messaging
~~~~~~~~~~~~~~~
Agent messaging is secured by the PKIAgentMessagingService. It provides methods
of encryption, signing, decryption and verification of the ACL messages. An
agent does not perform these operation itself, but gives it to the service. The
agent only marks a message whether it should be signed and/or encrypted.


Agent mobility
~~~~~~~~~~~~~~
When the agent moves between containers its code and data are exposed to
malicious modification. The PKIAgentMobilityService provides integrity of code
and data of the agent and certification of identity of the agent. It achieves
that by signing the code and the data with the private key of the container and
attaching the certificate into the field of the agent. For the agent to be
secured that way it needs to implement the Signable interface or, which is
recommended, extend one of the implementation: SMobileAgent or SAgent.
