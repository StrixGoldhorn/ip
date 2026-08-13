"""Run console UI tests and stop at the first output mismatch."""
import argparse, json, subprocess, sys
from pathlib import Path

def load_cases(path):
    text = path.read_text(encoding="utf-8")
    if path.suffix.lower() == ".json": return json.loads(text)
    start = text.index("<!-- TEST_CASES_JSON")
    start = text.index("\n", start) + 1
    return json.loads(text[start:text.index("-->", start)].strip())

def main():
    parser = argparse.ArgumentParser()
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument("--plan", type=Path); group.add_argument("--cases", type=Path)
    args = parser.parse_args(); passed = 0
    for number, case in enumerate(load_cases(args.plan or args.cases), 1):
        command = case["command"] if isinstance(case["command"], list) else case["command"].split()
        result = subprocess.run(command, input=case.get("input", ""), text=True, capture_output=True, shell=False)
        actual, expected = result.stdout, case["expected_output"]
        print(f"\n=== Test {number}: {case['name']} ===\nAim: {case['aim']}\n$ {' '.join(command)}")
        print(f"Input:\n{case.get('input', '')}\nOutput:\n{actual}")
        if result.returncode or actual != expected:
            print(f"RESULT: FAIL\nExpected output:\n{expected}")
            if result.stderr: print(f"stderr:\n{result.stderr}")
            print(f"Stopped after {passed} passed test(s)."); return 1
        print("RESULT: PASS"); passed += 1
    print(f"\nAll {passed} test(s) passed."); return 0

if __name__ == "__main__": sys.exit(main())
