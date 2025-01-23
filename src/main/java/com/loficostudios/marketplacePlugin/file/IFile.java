/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version MelodyApi
 */

package com.loficostudios.marketplacePlugin.file;

import org.jetbrains.annotations.NotNull;

public interface IFile {
    String getFileName();

    boolean getBoolean(@NotNull final String path);

    int getInt(@NotNull final String path);

    double getDouble(@NotNull final String path);

    long getLong(@NotNull final String path);

    void set(@NotNull final String path, @NotNull final Object newValue);

}
