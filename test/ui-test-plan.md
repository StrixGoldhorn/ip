# Console UI test plan

Run cases in order with `test-ui`. Use JDK 25 and compile first. Default launch command:

```text
java -cp out/production/ip_project Megatron
```

| Case | Aim | Inputs | Expected output |
|---|---|---|---|
| Exit command | Check that the program accepts the exit command and terminates. | `bye` | Display the banner, prompt, and exit message, then terminate. See the exact output in the JSON case below. |

The expected output below includes the final newline.

<!-- TEST_CASES_JSON
[
  {
    "name": "Exit command",
    "aim": "Check that the program accepts the exit command and terminates.",
    "command": ["java", "-cp", "out/production/ip_project", "Megatron"],
    "input": "bye\n",
    "expected_output": "____________________________________________________________\n   __  ___              __              \n  /  |/  /__ ___ ____ _/ /________  ___ \n / /|_/ / -_) _ `/ _ `/ __/ __/ _ \/ _ \\\n/_/  /_/\\__\\_, /\\_,_/\\__/_/  \\___/_//_/\n           /___/                        \n     Rawr! I'm Megatron.\n     What can I do for you?\n____________________________________________________________\n____________________________________________________________\n     Bye. Hope to see you again soon!\n____________________________________________________________\n"
  }
]
-->
