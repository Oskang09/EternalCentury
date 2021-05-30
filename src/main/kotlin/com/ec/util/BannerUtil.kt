package com.ec.util

import org.bukkit.Bukkit
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.block.banner.Pattern
import org.bukkit.block.banner.PatternType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BannerMeta
import java.util.function.Supplier

object BannerUtil {
    private var numberBanners: ArrayList<ItemStack> = ArrayList()

    init {
        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15bs0ls0ts0rs0dls0bo15
        numberBanners.add(build(Supplier {
            val patterns: MutableList<Pattern> =
                ArrayList()
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNLEFT))
            patterns.add(Pattern(DyeColor.BLACK, PatternType.BORDER))
            patterns
        }))

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15cs0tl0cbo15bs0bo15
        numberBanners.add(build(Supplier {
            val patterns: MutableList<Pattern> =
                ArrayList()
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_CENTER))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.SQUARE_TOP_LEFT))
            patterns.add(Pattern(DyeColor.BLACK, PatternType.CURLY_BORDER))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM))
            patterns.add(Pattern(DyeColor.BLACK, PatternType.BORDER))
            patterns
        }))

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15ts0mr15bs0dls0bo15
        numberBanners.add(build(Supplier {
            val patterns: MutableList<Pattern> =
                ArrayList()
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP))
            patterns.add(Pattern(DyeColor.BLACK, PatternType.RHOMBUS_MIDDLE))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNLEFT))
            patterns.add(Pattern(DyeColor.BLACK, PatternType.BORDER))
            patterns
        }))

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15bs0ms0ts0cbo15rs0bo15
        numberBanners.add(build(Supplier {
            val patterns: MutableList<Pattern> =
                ArrayList()
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP))
            patterns.add(Pattern(DyeColor.BLACK, PatternType.CURLY_BORDER))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT))
            patterns.add(Pattern(DyeColor.BLACK, PatternType.BORDER))
            patterns
        }))

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15ls0hhb15rs0ms0bo15
        numberBanners.add(build(Supplier {
            val patterns: MutableList<Pattern> =
                ArrayList()
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT))
            patterns.add(Pattern(DyeColor.BLACK, PatternType.HALF_HORIZONTAL_MIRROR))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE))
            patterns.add(Pattern(DyeColor.BLACK, PatternType.BORDER))
            patterns
        }))

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15bs0mr15ts0drs0bo15
        numberBanners.add(build(Supplier {
            val patterns: MutableList<Pattern> =
                ArrayList()
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM))
            patterns.add(Pattern(DyeColor.BLACK, PatternType.RHOMBUS_MIDDLE))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNRIGHT))
            patterns.add(Pattern(DyeColor.BLACK, PatternType.BORDER))
            patterns
        }))

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15bs0rs0hh15ms0ts0ls0bo15
        numberBanners.add(build(Supplier {
            val patterns: MutableList<Pattern> =
                ArrayList()
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT))
            patterns.add(Pattern(DyeColor.BLACK, PatternType.HALF_HORIZONTAL))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT))
            patterns.add(Pattern(DyeColor.BLACK, PatternType.BORDER))
            patterns
        }))

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15dls0ts0bo15
        numberBanners.add(build(Supplier {
            val patterns: MutableList<Pattern> =
                ArrayList()
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNLEFT))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP))
            patterns.add(Pattern(DyeColor.BLACK, PatternType.BORDER))
            patterns
        }))

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15ts0ls0ms0bs0rs0bo15
        numberBanners.add(build(Supplier {
            val patterns: MutableList<Pattern> =
                ArrayList()
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT))
            patterns.add(Pattern(DyeColor.BLACK, PatternType.BORDER))
            patterns
        }))

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15ls0hhb15ms0ts0rs0bs0bo15
        numberBanners.add(build(Supplier {
            val patterns: MutableList<Pattern> =
                ArrayList()
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT))
            patterns.add(Pattern(DyeColor.BLACK, PatternType.HALF_HORIZONTAL_MIRROR))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT))
            patterns.add(Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM))
            patterns.add(Pattern(DyeColor.BLACK, PatternType.BORDER))
            patterns
        }))

    }

    operator fun get(number: Int): ItemStack? {
        return if (number < 0 || number > 9) {
            ItemStack(Material.AIR)
        } else numberBanners[number].clone()
    }

    private fun build(patterns: Supplier<List<Pattern>>): ItemStack {
        val item = ItemStack(Material.BLACK_BANNER)
        val meta = Bukkit.getItemFactory().getItemMeta(Material.BLACK_BANNER) as BannerMeta?
        meta!!.patterns = patterns.get()
        item.itemMeta = meta
        return item
    }
}