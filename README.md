LSIS-AppGestion
===============

A graphical interface that allows you to manage members of a team, see charts about their distribution per supervisors, entry date, etc. It allows you to obtain a trombinoscope of the members, and to filter your data.

The actual version is 1.9.0.

It is a evolution of [this tutorial from code.makery](http://code.makery.ch/library/javafx-8-tutorial/ "code.makery tutorial") with several additionnal features:
- edit photo
- edit double click to edit
- trombinoscope
- additionnal charts
- additionnal filter
- see history of changes
- etc.

It was initially made for the [LSIS research laboratory](http://www.lsis.org/ "lsis home page").


# Build

This repo is built using the build.fxbuild file. You can build by importing the project in eclipse and build it, by directly executing the .fxbuild script.
The tutorial (link above) shows you an easy way to build its the "deployment" section.

# Usage

There are two executable files in the root directory:
- LsisAppGestion-1.9.0.jar --> java executable file
- LsisAppGestion.dmg --> MAC installation image

Double click on Jar or type :

```
java -Xmx[nb RAM]G -jar LsisAppGestion-{version}.jar
```

# Contacts

gael dot guibon at gmail.com
gael dot guibon at lsis.org

@2016 SRCMF LSIS-CNRS
