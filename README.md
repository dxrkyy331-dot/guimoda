# Base Hunt (Meteor Client addon)

Adds a **Base Hunt** tab to the Meteor GUI:

- `amethyst-chunk-finder` – chunks with many large amethyst buds
- `placed-block-finder` – chunks with crafting tables, furnaces, beds, etc.
- `light-source-finder` – chunks with many torches/lanterns
- `storage-tracers` – pick which storage types to box/trace

Use Meteor's built-in **Freecam** alongside these.

## Build on GitHub
1. Create a repo and push this folder.
2. Open the **Actions** tab. The *Build* workflow runs on every push.
3. Download the jar from the run's **Artifacts** (`basehunt-addon`).
4. Put the jar in `.minecraft/mods` next to Meteor Client and Fabric Loader.

## Build locally
`gradle build` (Java 21, Gradle 8.11+). Jar appears in `build/libs/`.

## Versions
`gradle.properties` targets Minecraft 1.21.4. To use another version, change
`minecraft_version`, `yarn_mappings`, `loader_version` and `meteor_version` to match your Meteor build.
Some Meteor API names (e.g. `ChunkDataEvent.chunk`, `RenderUtils.center`) shift between releases.

Check server rules before using ESP-style mods.
