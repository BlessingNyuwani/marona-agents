# Contributing

Thank you for helping improve the Marona Agent examples.

## Before opening a pull request

1. Open an issue for substantial behavior or architecture changes.
2. Keep examples provider-neutral and focused on one learning objective.
3. Never commit API keys, access tokens, customer data, or model output that
   contains personal information.
4. Add or update tests and documentation with every behavior change.
5. Run the complete repository verification command:

   ```bash
   make verify
   ```

## Pull requests

- Use a concise conventional commit title such as `feat: add streaming agent`.
- Explain the developer problem being solved and how it was verified.
- Keep generated files and unrelated formatting out of the change.
- Confirm that every changed example runs locally and in its Docker image.

By contributing, you agree that your contribution is licensed under the MIT
License used by this repository.
