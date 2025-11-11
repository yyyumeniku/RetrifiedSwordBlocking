# Retroified Sword Blocking

A comprehensive sword blocking mod for Minecraft 1.12.2 that brings back the classic pre-1.9 sword blocking mechanic for **ALL** swords, including vanilla, Tinkers' Construct, and other modded swords.

## Features

- ✅ **Universal Sword Support**: Works with ALL sword-type items, not just vanilla swords
- ✅ **Tinkers' Construct Integration**: Full support for all Tinkers' Construct weapons including:
  - Longsword, Rapier, Dagger, Cutlass, Broadsword, Katana
  - Cleaver, Battlesign, and other weapon-type tools
- ✅ **Modded Sword Detection**: Automatically detects modded swords by name patterns
- ✅ **Configurable**: Extensive configuration options to customize behavior
- ✅ **Performance Optimized**: Efficient item detection with minimal performance impact
- ✅ **Debug Logging**: Optional debug logging to help identify compatible items

## Supported Items

### Vanilla Swords
- All vanilla sword types (wood, stone, iron, gold, diamond)

### Tinkers' Construct Weapons (1.12.2)
- Longsword - Classic balanced sword
- Rapier - Fast, precise thrusting sword
- Dagger - Quick, lightweight blade
- Cutlass - Curved pirate sword
- Broadsword - Wide, heavy sword
- Katana - Sharp, elegant blade
- Cleaver - Heavy, brutal weapon
- Battlesign - Unique weapon tool

### Other Modded Swords
- Automatically detects items with "sword", "blade", "katana", "rapier", "dagger", "cutlass", "saber", "scimitar" in their names
- Supports items from any mod that follow common naming conventions

## Configuration

The mod creates a configuration file at `config/sword_block.cfg` with the following options:

```ini
# General settings
general {
    # Enable blocking for vanilla swords
    B:enableVanillaSwords=true
    
    # Enable blocking for Tinkers' Construct weapons
    B:enableTinkersWeapons=true
    
    # Enable blocking for other modded swords (detected by name patterns)
    B:enableModdedSwords=true
    
    # Enable debug logging to help identify items
    B:enableDebugLogging=false
}

# Custom item settings
custom {
    # Registry names of items that should be able to block
    S:customBlockingItems <
        examplemod:custom_sword
        anothermod:special_blade
     >
    
    # Registry names of items that should NOT be able to block
    S:excludedItems <
        examplemod:broken_sword
     >
}
```

## How Sword Detection Works

The mod uses multiple detection methods to identify sword-type items:

1. **Vanilla Sword Check**: Items that extend `ItemSword`
2. **Registry Name Matching**: Exact matches against known sword registry names
3. **Pattern Matching**: Items containing sword-related keywords
4. **Tinkers' Tool Detection**: 
   - NBT data analysis for Tinkers' tools
   - Class name checking for Tinkers' items
   - Attack damage attribute verification
5. **Custom Configuration**: User-defined items via config

## Installation

1. Make sure you have Minecraft Forge installed for 1.12.2
2. Download the mod JAR file
3. Place it in your `mods` folder
4. Launch Minecraft
5. (Optional) Configure the mod by editing `config/sword_block.cfg`

## Compatibility

- **Minecraft Version**: 1.12.2
- **Forge Version**: Compatible with all 1.12.2 Forge versions
- **Java Version**: Java 8+
- **Tinkers' Construct**: Full compatibility with all versions
- **Other Mods**: Compatible with most sword-adding mods

## Debug Mode

Enable `enableDebugLogging=true` in the config to see detailed information about which items are being detected as swords. This is useful for:
- Troubleshooting compatibility issues
- Identifying items that should be added to custom lists
- Understanding why certain items are or aren't working

## Credits

- Original concept inspired by ji_GGO's Classic Sword Blocking mod
- Enhanced for comprehensive mod compatibility and Tinkers' Construct support

## License

This project is open source. Feel free to modify and redistribute according to the license terms.
