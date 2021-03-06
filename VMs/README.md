# installation de l'ensemble Apache Accumulo, Apache Hadoop et Apache Zookeeper sur un cluster de 4 machines

## téléchargement des archives

``` bash
wget "http://www.eu.apache.org/dist/hadoop/common/stable/hadoop-2.9.0.tar.gz"
wget "http://www.eu.apache.org/dist/zookeeper/stable/zookeeper-3.4.6.tar.gz"
wget "http://www.eu.apache.org/dist/accumulo/1.8.1/accumulo-1.8.1-bin.tar.gz"
```


## Configuration d'Hadoop

### Modification du fichier 'hadoop-2.9.0/etc/hadoop/hadoop-env.sh'

``` bash
export JAVA_HOME=/usr/lib/jvm/default-java
export HADOOP_OPTS="$HADOOP_OPTS
-XX:-PrintWarnings -Djava.net.preferIPv4Stack=true"
```


export HADOOP_HEAPSIZE=2000

### Modification du fichier 'hadoop/etc/hadoop/core-site.xml'

La machine a l'adresse .186 contiendra le primary namenode
``` xml
<configuration>
        <property>
                <name>fs.defaultFS</name>
                <value>hdfs://145.239.142.186:9000</value>
        </property>
</configuration>
```


### Modification du fichier 'hadoop/etc/hadoop/hdfs-site.xml'


On fixe le nombre de réplications à 2.
On met le secondary namenode sur la machine 66, adresse 188.

``` xml
<configuration>
    <property>
		<name>dfs.replication</name>
        <value>2</value>
 	</property>
 	<property>
		<name>dfs.secondary.http.address</name>
        <value>145.239.142.188:50090</value>
 	</property>
</configuration>
```


### Modification du fichier 'hadoop/etc/hadoop/mapred-site.xml'


On met le Mapreduce job tracker sur la machine 63, à l'adresse 185.
``` xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
     <property>
         <name>mapred.job.tracker</name>
         <value>145.239.142.185:9001</value>
     </property>
     <property> 
	   <value>yarn</value> 
	   <name>mapreduce.framework.name</name> 
	</property> 
	<property> 
		 <name>mapreduce.jobhistory.address</name> 
		 <value>ns3104763:10020</value> 
	</property> 
	<property> 
		 <name>mapreduce.jobhistory.webapp.address</name> 
		 <value>3104763:19888</value> 
	</property> 
	<property> 
		 <name>yarn.app.mapreduce.am.staging-dir</name> 
		 <value>/user/app</value> 
	</property> 
	<property> 
		 <name>mapred.child.java.opts</name> 
		 <value>-Djava.security.egd=file:/dev/../dev/urandom</value> 
	</property> 
</configuration>
```

### Modification du fichier 'hadoop-2.9.0/etc/hadoop/yarn-site.xml'

On met le resource manager sur la machine 63, à l'adresse 185.

``` xml
<configuration> 
   <property> 
      <name>yarn.nodemanager.aux-services</name> 
      <value>mapreduce_shuffle</value> 
   </property> 
   <property> 
      <name>yarn.resourcemanager.hostname</name> 
      <value>ns3104763</value> 
   </property> 
   <property> 
       <name>yarn.nodemanager.local-dirs</name> 
       <value>file:/hadoop_data/yarn/local</value> 
   </property> 
   <property> 
       <name>yarn.nodemanager.log-dirs</name> 
       <value>file:/hadoop_data/yarn/log</value> 
   </property> 
   <property> 
       <name>yarn.nodemanager.resource.memory-mb</name> 
       <value>26000</value> 
   </property>
</configuration>
```


### Modification du fichier 'hadoop/etc/hadoop/yarn-env.sh'

JAVA_HEAP_MAX=-Xmx1000m
YARN_HEAPSIZE=2000
export YARN_RESOURCEMANAGER_HEAPSIZE=2000

### Modification du fichier 'hadoop-2.9.0/etc/hadoop/slaves'

on ajoute le nom des slaves = datanodes

ns3104765
ns3104764
ns3104763
ns3104766


### Formattage de HDFS

``` bash
./hadoop/bin/hdfs namenode -format
```
### Modification du fichier '/etc/hosts'

Pour chaque machine, on ajoute le nom et l'adresse ip des autres dans le 
fichier hosts


### Création d'une clé SSH pour chaque machine

On ajoute la clé publique dans le fichier 'authorized_keys' de toutes les autres machines


### Exécution de Hadoop et YARN

Sur la machine 64, adresse 186 :

``` bash
./sbin/start-dfs.sh
```

Sur la machine 63, adresse 186 :

``` bash
./sbin/start-yarn.sh
```




## Zookeeper

### Copie du fichier 'zookeeper/conf/zoo_sample.cfg' en 'zookeeper/conf/zoo.cfg'

### Lancer Zookeeper sur les machines : 63 (adresse 185), 65 (adresse 187) et 66 (adresse 188)

``` bash
./zookeeper/bin/zkserver start
```



## Accumulo

### Copie de la configuration

``` bash
cp accumulo-1.8.1/conf/examples/3GB/standalone/* accumulo-1.8.1/conf/
```

### Modification du fichier 'accumulo-1.8.1/conf/accumulo-env.sh'

On décommente la ligne 'export ACCUMULO_MONITOR_BIND_ALL="true"'

### Modification du fichier 'accumulo-1.8.1/conf/accumulo-site.xml'

On change les propiétés suivantes : 

``` xml
<property>
    <name>instance.volumes</name>
    <value>hdfs://145.239.142.186:9000/accumulo</value>
    <description>comma separated list of URIs for volumes. example: hdfs://localhost:9000/accumulo</description>
  </property>

  <property>
    <name>instance.zookeeper.host</name>
    <value>145.239.142.185:2181,145.239.142.187:2181,145.239.142.188:2181</value>
    <description>comma separated list of zookeeper servers</description>
  </property>

  <property>
    <name>instance.secret</name>
    <value>SECRET</value>
    <description>A secret unique to a given instance that all servers must know in order to communicate with one another.
      Change it before initialization. To
      change it later use ./bin/accumulo org.apache.accumulo.server.util.ChangeSecret --old [oldpasswd] --new [newpasswd],
      and then update this file.
    </description>
  </property>

  <property>
    <name>trace.token.property.password</name>
    <!-- change this to the root user's password, and/or change the user below $
    <value>root</value>
  </property>
```
  
### Masters

3 master : 145.239.142.187, 145.239.142.186, 145.239.142.188

### Tracers

3 tracers : 145.239.142.187, 145.239.142.188, 145.239.142.185

### Slaves

4 slaves : 145.239.142.185, 145.239.142.186, 145.239.142.187, 145.239.142.188

### Monitor

1 monitor : 145.239.142.187

### Garbage collector

3 GC : 145.239.142.185, 145.239.142.187, 145.239.142.188


## Sources

* https://cdiese.fr/configurer-un-cluster-hadoop-multi-nud/#duplicating_vm
* https://www.digitalocean.com/community/tutorials/how-to-install-the-big-data-friendly-apache-accumulo-nosql-database-on-ubuntu-14-04































