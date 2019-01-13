# Group 4-DIT355

# SOURCE CODE MODELER

Source Code Modeler is a software that allows developers to visualize source code into a diagram similar to a UML class diagram.
The primary purpose of the system is to convert a Java project into a visualization that can provide and overview of the structure of the project. The visualization will take the form of a diagram with similar but not identical syntax as a UML class diagram. Our system will only provide an indication that two components relate to each other, without specifying different types of relationships. The output diagram will provide developers with an overview of the project’s structure.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

- A computer with Windows installed.
- intellij installed.
- A router (It doesn’t work on public networks)
- Windows firewall needs to be turned off before running the program.
- In order to produce the visualization, Graphviz needs to be previously installed locally. Link: https://graphviz.gitlab.io/ 

### How to run it


- Pull the latest version of the program.
- Connect to the router. All the different nodes should be connected to the same router.
- Turn off windows firewall.
- Open Intellij and run the handler.java class.
- Select directory of the java project.
- Provide the 3 Ip addresses of the different 3 nodes in the user interface.
- Click the button distributed visualization.
- Close the program when you want to visualize another directory and repeat the steps.

- If the visualization is local and not distributed:
    - Pull the latest version of the program.
    - Connect to the router.
    - Turn off windows firewall.
    - Open Intellij and run the handler.java class.
    - Select the directory of the java project to visualize.
    - Click on local visualization button.

## Built With

* [intellij](https://www.jetbrains.com/idea/) - IDE Integrated Development Environment

## Authors

Fabian Fröding,
Salvatore Spanu Zucca,
Nuria Cara Navas,
Elsada Lagumdzic,
Melinda Ivók
