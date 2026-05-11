package com.kingmihailp.hexmotd.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public final class ColorParser {

    private ColorParser() {}

    /**
     * Parses a string with &-codes and &#RRGGBB hex colors into a {@link Component}.
     * <p>
     * Supported codes:
     * <ul>
     *   <li>{@code &#RRGGBB} – 24-bit hex color</li>
     *   <li>{@code &0-9 / &a-f} – legacy Minecraft colors</li>
     *   <li>{@code &l} bold, {@code &o} italic, {@code &n} underline,
     *       {@code &m} strikethrough, {@code &k} obfuscated, {@code &r} reset</li>
     * </ul>
     */
    public static Component parse(String text) {
        if (text == null || text.isEmpty()) return Component.empty();

        MutableComponent root = Component.empty();
        Style currentStyle = Style.EMPTY;

        int i = 0;
        StringBuilder buffer = new StringBuilder();

        while (i < text.length()) {
            char c = text.charAt(i);

            if (c == '&' && i + 1 < text.length()) {
                char next = text.charAt(i + 1);

                // &#RRGGBB hex color
                if (next == '#' && i + 7 < text.length()) {
                    String hex = text.substring(i + 2, i + 8);
                    if (isHex(hex)) {
                        flushBuffer(root, buffer, currentStyle);
                        int rgb = Integer.parseInt(hex, 16);
                        // Hex color resets formatting modifiers for a clean look
                        currentStyle = Style.EMPTY.withColor(TextColor.fromRgb(rgb));
                        i += 8;
                        continue;
                    }
                }

                // Standard &X code
                char lower = Character.toLowerCase(next);
                if (isValidCode(lower)) {
                    flushBuffer(root, buffer, currentStyle);
                    currentStyle = applyCode(currentStyle, lower);
                    i += 2;
                    continue;
                }
            }

            buffer.append(c);
            i++;
        }

        flushBuffer(root, buffer, currentStyle);
        return root;
    }

    private static void flushBuffer(MutableComponent root, StringBuilder buffer, Style style) {
        if (buffer.length() > 0) {
            root.append(Component.literal(buffer.toString()).withStyle(style));
            buffer.setLength(0);
        }
    }

    private static boolean isHex(String s) {
        if (s.length() != 6) return false;
        for (char c : s.toCharArray()) {
            if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidCode(char c) {
        return (c >= '0' && c <= '9')
            || (c >= 'a' && c <= 'f')
            || c == 'k' || c == 'l' || c == 'm'
            || c == 'n' || c == 'o' || c == 'r';
    }

    private static Style applyCode(Style style, char code) {
        return switch (code) {
            case '0' -> style.withColor(fromLegacy(ChatFormatting.BLACK));
            case '1' -> style.withColor(fromLegacy(ChatFormatting.DARK_BLUE));
            case '2' -> style.withColor(fromLegacy(ChatFormatting.DARK_GREEN));
            case '3' -> style.withColor(fromLegacy(ChatFormatting.DARK_AQUA));
            case '4' -> style.withColor(fromLegacy(ChatFormatting.DARK_RED));
            case '5' -> style.withColor(fromLegacy(ChatFormatting.DARK_PURPLE));
            case '6' -> style.withColor(fromLegacy(ChatFormatting.GOLD));
            case '7' -> style.withColor(fromLegacy(ChatFormatting.GRAY));
            case '8' -> style.withColor(fromLegacy(ChatFormatting.DARK_GRAY));
            case '9' -> style.withColor(fromLegacy(ChatFormatting.BLUE));
            case 'a' -> style.withColor(fromLegacy(ChatFormatting.GREEN));
            case 'b' -> style.withColor(fromLegacy(ChatFormatting.AQUA));
            case 'c' -> style.withColor(fromLegacy(ChatFormatting.RED));
            case 'd' -> style.withColor(fromLegacy(ChatFormatting.LIGHT_PURPLE));
            case 'e' -> style.withColor(fromLegacy(ChatFormatting.YELLOW));
            case 'f' -> style.withColor(fromLegacy(ChatFormatting.WHITE));
            case 'k' -> style.withObfuscated(true);
            case 'l' -> style.withBold(true);
            case 'm' -> style.withStrikethrough(true);
            case 'n' -> style.withUnderlined(true);
            case 'o' -> style.withItalic(true);
            case 'r' -> Style.EMPTY;
            default  -> style;
        };
    }

    private static TextColor fromLegacy(ChatFormatting formatting) {
        return TextColor.fromLegacyFormat(formatting);
    }
}
