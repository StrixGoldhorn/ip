# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Basic, trivial
* IDE and level of expertise: IntelliJ IDEA, basic

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Documentation

Unless otherwise stated, you are to respond in and write all documentation adhering to ASD-STE100 Simplified Technical English. For complex queries and responses, you are to ask the user if using more technical language is ok.

## SWE Practices

You are to OOP-ify where needed. You have to justify whether something needs to be OOP, or not.
You are an expert software engineer and expected to follow all good SWE practices.

## UI test workflow after code updates

After every code update:

1. Review `test/ui-test-plan.md` and update it if the code change adds, removes, or changes user-visible console behaviour. You must tell the user what has been modified, and the reasoning.
2. Invoke the project-local `$test-ui` skill and run the documented UI test cases.

Do not consider a code update complete until both checks are done. If the UI test plan has a placeholder or an expected-output mismatch, report it clearly.

You are to report the success or failure of tests in every response that modifies code.
