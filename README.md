# Lookup Service - Level 3

Your task is to build a backend service that implements the [Lookup Service REST API](https://infra.devskills.app/lookup/api/1.0.0) and integrates with the [Credit Data REST API](https://infra.devskills.app/credit-data/api/1.0.0) to aggregate historical credit data.

Please agree with the hiring team regarding the tech stack choice.

Make sure to read carefully the section below about what are the task expectations.

## Before you get started

### If you run into a problem

Need help? Head over to [our community on GitHub](https://github.com/orgs/DevSkillsHQ/discussions/categories/help) to get assistance.

### Import boilerplate

Follow [this link](https://docs.devskills.co/collections/85-the-interview-process/articles/342-importing-challenge-boilerplate) to get the boilerplate code for your tech stack to configure a minimal setup for running the E2E tests.

<details>
<summary>Alternatively, use the manual setup.</summary>

1. Update the `apiUrl` (where your backend runs) in [cypress.json](cypress.json).
2. Update the [`build`](package.json#L5) and [`start`](package.json#L6) scripts in [package.json](package.json) to respectively build and start your app.

</details>

### Get familiar with the APIs

- [Lookup Service REST API](https://infra.devskills.app/lookup/api/1.0.0) - the one that's supposed to be implemented.
- [Credit Data REST API](https://infra.devskills.app/credit-data/api/1.0.0) - the one to integrate with to get the credit data from. Available SSNs: `424-11-9327`, `553-25-8346`, `287-54-7823`.

### Try running the API tests

<details>
<summary>Remotely on the pipeline</summary>

Create and switch to a new `implementation` branch and push your code. This will trigger a new pipeline run which will execute the tests.

Check the 'Actions' tab to see the historical runs.

</details>


<details>
<summary>Locally with Docker (Mac & Windows only)</summary>

#### Prerequisites

- [Install Docker](https://www.docker.com/get-started)
- Start your app

#### Run the tests
```bash
 docker run --add-host host.docker.internal:host-gateway -v $PWD:/e2e -w /e2e cypress/included:3.4.0
```

You can either use the console output or generated screenshots/videos (*check the newly created files that appear after a test run*) to troubleshoot the test results.


</details>

<details>
<summary>Locally with npm</summary>

#### Prerequisites

1. [Install node](https://nodejs.org/en/)
2. When in the project's root, run: `sed 's/host.docker.internal/localhost/g' cypress.json > cypress.json.tmp && mv cypress.json.tmp cypress.json`  
3. Start your app

#### Run the tests
```bash
 npm run test
```

You can either use the console output or generated screenshots/videos (*check the newly created files that appear after a test run*) to troubleshoot the test results.

</details>

### What we expect from you

1. Make the provided API tests pass.
2. Implement DB caching controlled by `Cache-Control` headers, provided both in the requests to your service, as well as received in the responses from Credit Data API. 
    1. We can limit ourselves here only to `no-store` and `max-age` directives. Please make yourself familiar with their semantics using the following links: 
        * `no-store` [request](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control#no-store_2) and [response](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control#no-store) directives
        * `max-age` [request](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control#max-age_2) and [response](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control#max-age) directives
    2. These `control-cache` headers will be returned only by `/personal-details` endpoint
3. Be mindful about error handling. The API tests are not restricting any particular service behaviour so it is up to you to choose a solution that feels right.
4. Avoid duplication and extract re-usable modules where it makes sense. We want to see your approach to creating a codebase that is easy to maintain.
5. Unit test one module of choice. There is no need to test the whole app, as we only want to understand what you take into consideration when writing unit tests.
6. Push your code to the new `implementation` branch. We encourage you to commit and push your changes regularly as it's a good way for you to showcase your thinking process.
7. Create a new pull request, but please **do not merge it**.
8. Document the tech decisions you've made by creating a new review on your PR ([see how](https://www.loom.com/share/94ae305e7fbf45d592099ac9f40d4274)).
9. Await further instructions from the hiring team.

## Time estimate

About **2-3h** depending on your experience level + the time to set up the project/environment (go with one of the provided boilerplates to move faster).

Also, there is no countdown. The estimate is for you to plan your time.

---

Authored by [Rafa Paliwoda](https://devskills.co/authors/rafa-paliwoda) via [DevSkills](https://devskills.co).

How was your experience? **[Don't hesitate to give us a shout](https://github.com/orgs/DevSkillsHQ/discussions/categories/feedback)**.


