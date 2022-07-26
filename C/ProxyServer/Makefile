run: clean client proxy

client: Client.c dns.c
	gcc -o Client Client.c dns.c

proxy: ServerProxy.c dns.c lib/libstring.c
	gcc -o ServerProxy ServerProxy.c lib/libstring.c -lpthread -lconfig

clean:
	rm -rf *.o ServerMain ServerProxy Client