package invtweaks.integration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Checks if the NEI or JEI item lists are visible on the screen.
 * Used to make sure InvTweaks buttons do not overlap with the item list.
 */
public class ItemListChecker {
	@Nullable
    private Method neiHidden;
	@Nullable
    private Method jeiShown;

	private boolean visible = false;
	private boolean wasVisible = false;

	public ItemListChecker() {
		this.neiHidden = getNeiHidden();
		this.jeiShown = getJeiShown();
	}

	public boolean isVisible() {
		wasVisible = visible;
		visible = isNeiVisible() || isJeiVisible();
		return visible;
	}

	public boolean wasVisible() {
		return wasVisible;
	}

	private static Method getNeiHidden() {
		if (Loader.isModLoaded("nei")) {
			try {
				Class<?> clientConfig = Class.forName("codechicken.nei.NEIClientConfig");
				if (clientConfig != null) {
					return clientConfig.getMethod("isHidden");
				}
			} catch (@NotNull ClassNotFoundException | NoSuchMethodException | SecurityException ignored) {
				return null;
			}
		}
		return null;
	}

	private static Method getJeiShown() {
		if (Loader.isModLoaded("jei")) {
			try {
				Class<?> clientConfig = Class.forName("mezz.jei.config.Config");
				if (clientConfig != null) {
					return clientConfig.getMethod("isOverlayEnabled");
				}
			} catch (@NotNull ClassNotFoundException | NoSuchMethodException | SecurityException ignored) {
				return null;
			}
		}
		return null;
	}

	private boolean isNeiVisible() {
		if (neiHidden != null) {
			try {
				return !(Boolean) neiHidden.invoke(null);
			} catch (@NotNull IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				neiHidden = null;
				return false;
			}
		}
		return false;
	}

	private boolean isJeiVisible() {
		if (jeiShown != null) {
			try {
				return (Boolean) jeiShown.invoke(null);
			} catch (@NotNull IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				jeiShown = null;
				return false;
			}
		}
		return false;
	}
}
