# Megatron User Guide

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Finding tasks

Use `find` to search task descriptions. The search ignores letter case and accepts partial words.
It also accepts a one-character insertion, deletion, or substitution for search terms that have at
least three characters. If you enter multiple terms, each term must match the description, but the
terms can occur in a different order.

Command format: `find <query>`

Examples:

- `find BOO` matches `read book` because the search ignores letter case and accepts partial words.
- `find bok` matches `read book` because `bok` is one edit away from `book`.
- `find book read` matches `read book` because both terms occur in the description.
- `find bk` does not match `book` because terms shorter than three characters do not use fuzzy
  matching.

Megatron lists matching tasks in their original order and shows each task's original list number.
You can use that number with commands such as `mark`, `unmark`, and `delete`. Searching does not
change the stored task list.

```text
     Here are the matching tasks in your list:
     2.[T][ ] read book
```

If no task matches, Megatron shows:

```text
     No tasks found matching that description.
```


## Feature XYZ

// Feature details
