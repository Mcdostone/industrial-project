# Configuration d'Accumulo pour envoyer ses métriques dans Graphite

## Dans ACCUMULO_HOME/conf/hadoop-metrics2-accumulo.properties :

- décommenter tous les sink que l'on veut

- décommenter les lignes de configuration Graphite. C'est là que l'on peut modifier l'adresse et le port du serveur

# Lancer le daemon graphite

``` bash
docker run -d\
 --name graphite\
 --restart=always\
 -p 80:80\
 -p 2003-2004:2003-2004\
 -p 2023-2024:2023-2024\
 -p 8125:8125/udp\
 -p 8126:8126\
 graphiteapp/graphite-statsd
```

# Lancer le daemon grafana 

``` bash
docker run -d -p 3000:3000 grafana/grafana
```