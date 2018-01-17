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
export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_151

export HADOOP_OPTS="$HADOOP_OPTS
-XX:-PrintWarnings -Djava.net.preferIPv4Stack=true"
```


### Modification du fichier 'hadoop-2.9.0/etc/hadoop/core-site.xml'

La machine a l'adresse .186 contiendra le primary namenode
```
<configuration>
        <property>
                <name>fs.defaultFS</name>
                <value>hdfs://145.239.142.186:9000</value>
        </property>
</configuration>
```


### Modification du fichier 'hadoop-2.9.0/etc/hadoop/hdfs-site.xml'

On fixe le nombre de réplications à 3, valeur optimale pour les performances.
``` xml
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>3</value>
    </property>
    <property>
        <name>dfs.name.dir</name>
        <value>hdfs_storage/name</value>
    </property>
    <property>
        <name>dfs.data.dir</name>
        <value>hdfs_storage/data</value>
    </property>
    <property>
        <name>dfs.datanode.synconclose</name>
        <value>True</value>
    </property>
</configuration>
```


### Modification du fichier 'hadoop-2.9.0/etc/hadoop/mapred-site.xml'

On met le Mapreduce job tracker sur la même machine qui a le primary namenode.
``` xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
     <property>
         <name>mapred.job.tracker</name>
         <value>145.239.142.186:9001</value>
     </property>
     <property> 
	   <value>yarn</value> 
	   <name>mapreduce.framework.name</name> 
	</property> 
	<property> 
		 <name>mapreduce.jobhistory.address</name> 
		 <value>ns3104764:10020</value> 
	</property> 
	<property> 
		 <name>mapreduce.jobhistory.webapp.address</name> 
		 <value>3104764:19888</value> 
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
``` xml
<configuration> 
   <property> 
      <name>yarn.nodemanager.aux-services</name> 
      <value>mapreduce_shuffle</value> 
   </property> 
   <property> 
      <name>yarn.resourcemanager.hostname</name> 
      <value>ns3104764</value> 
   </property> 
   <property> 
      <name>yarn.resourcemanager.bind-host</name> 
      <value>0.0.0.0</value> 
   </property> 
   <property> 
     <name>yarn.nodemanager.bind-host</name> 
     <value>0.0.0.0</value> 
   </property> 
   <property> 
       <name>yarn.nodemanager.aux-services.mapreduce_shuffle.class</name> 
       <value>org.apache.hadoop.mapred.ShuffleHandler</value> 
   </property> 
   <property> 
       <name>yarn.log-aggregation-enable</name> 
       <value>true</value> 
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
       <name>yarn.nodemanager.remote-app-log-dir</name> 
       <value>hdfs://ns3104764:9000/var/log/hadoop-yarn/apps</value> 
   </property> 
</configuration>
```


### Modification du fichier 'hadoop-2.9.0/etc/hadoop/slaves'

on ajoute le nom des slaves = datanodes

ns3104765
ns3104763
ns3104766


### Formattage de HDFS

``` bash
./bin/hdfs namenode -format
```
### Modification du fichier '/etc/hosts'

Pour chaque machine, on ajoute le nom et l'adresse ip des autres dans le 
fichier hosts


### Création d'une clé SSH pour le NameNode

On ajoute la clé publique dans le fichier 'authorized_keys' de toutes les machines


### Exécution de Hadoop et YARN

``` bash
./sbin/start-dfs.sh
```

``` bash
./sbin/start-yarn.sh
```


## Sources

* https://cdiese.fr/configurer-un-cluster-hadoop-multi-nud/#duplicating_vm
* https://www.digitalocean.com/community/tutorials/how-to-install-the-big-data-friendly-apache-accumulo-nosql-database-on-ubuntu-14-04






























