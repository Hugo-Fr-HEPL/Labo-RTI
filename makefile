.SILENT:
OBJET = LibReseaux.o
GPP = g++ -m64 -Wall -D CPP -I lib/

ALL: Serveur Client


# Executable
Serveur: $(OBJET) serveur.cpp
	echo Creation de Serveur
	$(GPP) -o Serveur serveur.cpp $(OBJET)

Client: $(OBJET) client.cpp
	echo Creation de Client
	$(GPP) -o Client client.cpp $(OBJET)


# Objets
LibReseaux.o: lib/LibReseaux.cpp lib/LibReseaux.h
	echo Creation de LibReseaux.o
	$(GPP) lib/LibReseaux.cpp -c