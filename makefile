.SILENT:

OBJET = LibReseaux.o
GPP = g++ -m64 -Wall -D SUN -D CPP -lsocket -lnsl

serveur: $(OBJET) serveur.c
	echo Creation de Serveur
	$(GPP) -o Serveur serveur.c $(OBJET)

client: $(OBJET) client.c
	echo Creation de client
	$(GPP) -o client client.c $(OBJET)

LibReseaux.o: LibReseaux.c LibReseaux.h
	echo Creation de LibReseaux.o
	$(GPP) LibReseaux.c -c

