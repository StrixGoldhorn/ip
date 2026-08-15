# Megatron project

Megatron is a Java chatbot application. Given below are instructions on how to use it.

## AI Usage Disclaimer
- Codex used to generate code for test cases (test objectives were written by human).
- Codex also used to generate code from human descriptive language of issue and software architecture.
- Approx. AI-4 as defined in [CS2103T site](https://nus-cs2103-ay2627-s1.github.io/website/admin/courseExpectations.html)

AI- 4: `Visualize and compare: Think of how you would do the task manually. Get AI to do it. Compare the solution you 'imagined' with the one AI produced.`

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Megatron.java` file, right-click it, and choose `Run Megatron.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see the banner below:
   ```
      __  ___              __
     /  |/  /__ ___ ____ _/ /________  ___
    / /|_/ / -_) _ `/ _ `/ __/ __/ _ \/ _ \
   /_/  /_/\__/\_, /\_,_/\__/_/  \___/_//_/
              /___/
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
