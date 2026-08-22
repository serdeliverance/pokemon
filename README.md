# Pokemon

You can find more details on how to run each project in their respective folders.

## Stack Overview

// TODO

## Architecture

// TODO

## Timeline

// TODO

## Out of scope (or things I wish it were added)

- CI/CD flow with github actions, deploying containarized components images (poke-api, poke-fe, postgres)
- authentication with OAuth2 for federated usage, not having to store user credentials into the DB
- circuit breaker mechanism to be resilient in case of third party service shortages
- rate limit on the backend side

## Branching approach

Feature branch integrating into `main` (not using a `develop` branch to not overcomplicate the flow).
The following branch name convention was used: `ft-COMPONENT/feature-name`, when component can be:
- BE: poke-api
- FE: poke-fe
- TMS: time-management-system
All branches are kept alive, so the thought process can be tracked.
