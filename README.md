# ProcGen

This project is a Minecraft clone, I originally created this project
in order to understand procedural generation and I thought creating
a MC clone was the best easy way to visualize this type of
generation.

## Play (version 0.1.1)

- Escape to switch pause mode
- Use mouse to move the camera
- Press `L` to generate terrain around the camera
- Press `I` to instantiate a **PIG**
- Use `ZQSD` (or WASD) to move
- Space and Shift to move verticaly
- Press `F11` to toggle fullscreen
- Use arrows keys to rotate the first PIG's head
- Press `K` to randomly move the first PIG
- Press `P` to send debug message for the first PIG (dev)

> If you want to customize controls, you can edit the `options.json` (after the first start) next to downloaded jar file.
> You can use the [GLFW page about keys ids](https://www.glfw.org/docs/latest/group__keys.html) to know numbers to put in the configuration file to change control keys.
> This config file will be supported in future version.

> Do not forget to save your custom settings before launching a new version of the game, because I add lot of debug keys to the game each release.

## This content is coming

- [x] Motion entities
- [x] Pigs
  - [x] Model
  - [x] Animations ***(New in 0.1.1)***
  - [ ] AI
- [ ] Asynchronous (multi-threaded) chunk generation
- [ ] Asynchronous world chunk saving on file system
- [ ] Items
- [ ] All-model falling blocks
- [ ] TNT

## Contribute

This game is currently in development, it is not playable but it is
already possible to move through the world.

For development I'm using Gradle (use wrapper with `./gradrew` and `./gradlew.bat`) and IntelliJ IDEA as IDE.