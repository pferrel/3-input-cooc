#Multiple Indicator Creation

This is an example of how to create more that two indictors from more than two user interaction types with Mahout. We will use very simple hand created example data for one might see in an ecommerce application. The application records three interactions for item-purchase, item-detail-view, and category-preference (search for or click on a category). 

*spark-itemsimilarity* will handle two inputs but here we have three and rather than running *spark-itemsimilarity* twice we will create our own app to do it.

##Setup
In order to build and run the CooccurrenceDriver you need to install the following:

* Install the Java 7 JDK from Oracle. Mac users look here: [Java SE Development Kit 7u72](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html).
* Install sbt (simple build tool) 0.13.x for <a href="Installing-sbt-on-Mac.html">Mac</a>, <a href="Installing-sbt-on-Windows.html">Windows</a>,
<a href="Installing-sbt-on-Linux.html">Linux</a>,  or
<a href="Manual-Installation.html">manual installation</a>.
* Install [Mahout](http://mahout.apache.org/general/downloads.html). Don't forget to setup MAHOUT_HOME and MAHOUT_LOCAL

##Build
Building the examples from project's root folder:

    $ sbt pack

This will automatically set up some launcher scripts for the driver. To run execute

    $ target/pack/bin/cooc
    
The driver will execute in Spark standalone mode one the provided sample data and output log information including various information about the input data. The output will be in /path/to/3-input-cooc/data/indicators/*indicator-type*

##CooccurrenceDriver

This driver takes three actions in three separate input files. The input is in tuple form (user-id,item-id) one per line. It calculates all cooccurrence and cross-cooccurrence indicators. The sample actions are trivial hand made examples with somewhat intuitive data.

Actions:

 1. **Purchase**: user purchases
 2. **View**: user product details views
 3. **Category**: user preference for category tags
 
Indicators:

 1. **Purchase cooccurrence**: may be interpretted as a list if similar items for each item. Similar in terms of which users purchased them.
 2. **View cross-cooccurrence**: may be interpretted as a list of similar items in terms of which users viewed the item where the view led to a purchase.
 3. **Category cross-cooccurrence**: may be interpretted as a list of similar categories in terms of which users preferred the category and this led to a purchase.

##Data
Mahout has reader traits that will read text delimited files. Input for *spark-itemsimilarity* and this CooccurrenceDriver are tuples of (user-id,item-id) with one line per tuple. The inputs for CooccurrenceDriver are files but in *spark-itemsimilarity* they may be directories of "part-xxxxx" files. These can be found in the ```data``` directory.

##Using a Debugger
To build and run this example in a debugger like IntelliJ IDEA. Install from the IntelliJ site and add the Scala plugin.

Open IDEA and go to the menu File->New->Project from existing sources->SBT->/path/to/3-input-cooc. This will create an IDEA project from ```build.sbt``` in the root directory.

At this point you may create a "Debug Configuration" to run. In the menu choose Run->Edit Configurations. Under "Default" choose "Application". In the dialog hit the elipsis button "..." to the right of "Environment Variables" and fill in your versions of JAVA_HOME, SPARK_HOME, and MAHOUT_HOME. In configuration editor under "Use classpath from" choose root-3-input-cooc module. 

![image](http://mahout.apache.org/images/debug-config.png)

Now choose "Application" in the left pane and hit the plus sign "+". give the config a name and hit the elipsis button to the right of the "Main class" field as shown.

![image](http://mahout.apache.org/images/debug-config-2.png)


After setting breakpoints you are now ready to debug the configuration. Go to the Run->Debug... menu and pick your configuration. This will execute using a local standalone instance of Spark.
