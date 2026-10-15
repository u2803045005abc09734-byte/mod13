# Particular Waterfall Only (Fabric 26.3)

A small standalone client mod containing **only Particular's animated waterfall-cascade particle**.

## What is included

- The original 12 animation textures (`cascade_0.png` through `cascade_11.png`) extracted unchanged from `Particular 1.1.1+1.21.4`.
- A port of Particular's `CascadeParticle` behavior to Minecraft 26.3's `SingleQuadParticle` API.
- Detection of waterfall-edge positions and emission of the animated cascade.

## What is deliberately omitted

No generated droplet sprites, no generic water-spray particle, no leaves, fireflies, bubbles, splash rings, chest effects, configuration UI, or unrelated Particular features.

## Installation

This is a **client-only** mod. Build it through GitHub Actions and put the resulting JAR in the `mods` folder of each Minecraft 26.3 Fabric client that should render the waterfall effect. Use Java 25, Fabric Loader 0.19.5+, and Fabric API for Minecraft 26.3.

## Build

In GitHub, commit the full project and run **Actions → Build Particular Waterfall Only (Fabric 26.3)**. Download the `Particular-Waterfall-Only-Fabric-26.3` artifact from the successful run.

## Attribution and license

The cascade animation textures and original effect concept/code are from **Particular** by Chailotl:
- Upstream: https://github.com/Chailotl/particular
- License: GNU LGPL v3.0

This standalone port modifies the original cascade effect for Minecraft 26.3 and omits the other effects. The original LGPL-3.0 license is included as `LICENSE`.
