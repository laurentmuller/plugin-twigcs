# Plugin-Twigcs
[![Build Status](https://travis-ci.org/laurentmuller/plugin-twigcs.svg?branch=master)](https://travis-ci.org/laurentmuller/plugin-twigcs)

An Eclipse plugin to validate Twig (*.twig) files with the [Twigcs](https://github.com/friendsoftwig/twigcs) component.

Errors are visible in the **Editor**.

![Alt Editor](docs/editor.png)

And displayed in the **Problems view**.

![Alt Problems](docs/problems.png)

## Installation

  ### Installation of Twigcs

  - Install Twigcs component globally as explain in the [Twigcs Github site](https://github.com/friendsoftwig/twigcs). 

    ```bash
    composer global require friendsoftwig/twigcs
    ```

  ### Eclipse Installation from the Update Site

  - Start Eclipse PDT and select the menu **Help** -> **Install New Software...**

  - Click the **Add....** button.

  - Enter the location as the image below.
  
    ![Add Update Site](docs/add_repository_site.png)
    
  - Select the newly added repository. 

  - Check the the Twigcs category check box.

    ![Install](docs/update.png)

  - Follow the wizard instructions.

  - Update the [Workspace preferences](#workspace-preferences) to define the path to the Twigcs batch file.

  - Update your [Project properties](#project-properties) to define witch folders are validate.
    
  ### Eclipse Installation from the Zip file

  - Download the update site zip file from the releases tab.

  - Start Eclipse PDT and select the menu **Help** -> **Install New Software...**

  - Click the **Add....** button.

  - Click the **Archive...** button and select the downloaded zip file.

    ![Add Repository](docs/add_repository_zip.png)

  - Select the newly added repository.

  - Check the the Twigcs category check box.

  - Follow the wizard instructions.

  ### Installation in the dropins folder

  - Download the jar file (nu.bibi.twigcs-x.y.z.jar) from the releases tab to your local drive.
  - Copy that file to the dropins folder of your Eclipse PDT installation.
  - Restart Eclipse.

## Workspace preferences

The workspace preferences allow user to define how the Twigcs component run. The most important and required property is the path to the batch file.

![Alt Workspace preferences](docs/preferences.png)


## Project properties

To enable validation, You must add the Twigcs nature to the project.

- Select the project You want to update in the Explorer View.
- Display the context menu.
- Select **Configure** -> **Enable Twigcs validation**.

![Install](docs/enable_twigcs.png)


For each project, user can select witch folders are included for the validation or are excluded. The user must select at least one included folder .

![Alt  Project properties](docs/properties.png)
