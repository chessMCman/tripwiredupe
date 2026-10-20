# TripWire Dupe Restorer

一个 NeoForge 1.21.1 模组，还原 MC-129055 的「剪刀剪绊线 → disarm（解除武装）」行为，用于恢复刷线机（绊线钩线复制机）。

## 功能

- 用剪刀剪**连接到绊线钩、且未 disarm** 的绊线时，只把它置为 `disarmed=true` 并留在原地，**不破坏、不掉落**。
- 未连钩的线、或已经 disarm 的线，剪刀照常破坏掉落（不影响原版剪刀功能）。
- 仅在服务端逻辑生效（`MixinServerPlayerGameMode`）。

## 刷线机完整原理

刷线机需要两件事配合：

1. **本模组**：让剪刀剪连钩的线时只 disarm 而不破坏（还原 MC-129055）。
2. **服务端**（Mohist/Paper 系）开启绊线解除武装漏洞开关，还原 `calculateState` 无条件写回线的行为（还原 MC-59471）。

### Mohist / Youer / Paper 服务端配置

编辑 `<服务端根目录>/paper-config/paper-global.yml`（Mohist 用 `paper-config` 目录，不是 `config`），将：

```yaml
unsupported-settings:
  allow-tripwire-disarming-exploits: true
```

> 该项的官方注释是 "This also allows for dupes"（允许刷物品），默认 `false`。

### 原版单人 / 纯 NeoForge 服务端

原版 `calculateState` 本身无条件写回线，无需额外配置，装本模组即可刷线。

## 构建

```bash
./gradlew build
```

产物在 `build/libs/tripwiredupe-1.21.1-1.0.0.jar`。

## 安装

- 客户端：放入 `.minecraft/versions/<版本>/mods/`（单人刷线需要）。
- 服务端：放入 `mods/`，并按上文开启 `allow-tripwire-disarming-exploits`。

## 许可证

MIT
