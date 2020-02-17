# Plugin-twigcs
An Eclipse plugin to validate Twig (*.twig) files with the [Twigcs](https://github.com/friendsoftwig/twigcs) component.

Errors are visible in the **Editor**.

![Alt Editor](docs/editor.png)

And displayed in the **Problems view**.

![Alt Problems](docs/problems.png)

## Installation

- Install Twigcs component globally as explain in the [Twigcs Github site](https://github.com/friendsoftwig/twigcs). 

  ```bash
  composer global require friendsoftwig/twigcs
  ```

- Download the update site from the release and extract content to locale.

- Start Eclipse PDT and select the menu **Help** -> **Install New Software...**

- Select the extracted directory.

- Copy the jar file to the dropins folder of your Eclipse PDT installation.

- Start Eclipse PDT.

- Update the [Workspace preferences](##Workspace preferences) to define the path to the Twigcs batch file.

- Update your [Project properties](##Project properties) to define witch folders are validate.

## Workspace preferences

The workspace preferences allow user to define how the Twigcs component run. The most important and required property is the path to the batch file.

![Alt Workspace preferences](docs/preferences.png)


## Project properties

For each project, user can select witch folders are included for the validation or are excluded. The user must select at least one included folder .

![Alt  Project properties](docs/properties.png)
