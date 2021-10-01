.SILENT:
OBJET = reseaux.o
GPP = g++ -m64 -Wall -D CPP -Ilib/ #-lnsl -lsocket


# Executable
Serv: $(OBJET) serveur_checkin.cpp
	echo Creation de Serv
	$(GPP) -o Serv serveur_checkin.cpp $(OBJET)

# Deprecated Tests
TEST: Serveur Client
Serveur: $(OBJET) serveur.cpp
	echo Creation de Serveur
	$(GPP) -o Serveur serveur.cpp $(OBJET)
Client: $(OBJET) client.cpp
	echo Creation de Client
	$(GPP) -o Client client.cpp $(OBJET)


# Objets
reseaux.o: lib/reseaux.cpp lib/reseaux.h
	echo Creation de Reseaux.o
	$(GPP) lib/reseaux.cpp -c