# RawBt-and-ESCPOS-coffee

The Android application [RawBT ESC / POS thermal printer driver (BT, WIFI, USB)](https://play.google.com/store/apps/details?id=ru.a402d.rawbtprinter) can be used for transferring binary data (ready for printing) for free.

The source code of the application demonstrating all the available ways to interact with RawBT:
[DemoRawBtPrinter](https://github.com/402d/DemoRawBtPrinter)


[escpos-coffee](https://github.com/anastaciocintra/escpos-coffee) - Java library for ESC/POS

## About

This example illustrates how to use the library (escpos-coffee) and application (RawBT).

## Warning

The graphic part of the library cannot be used in Android.

```
package com.github.anastaciocintra.escpos.image;
import java.awt.image.BufferedImage;
```

[how-to-add-java-awt-image-package-in-android](https://stackoverflow.com/questions/6344654/how-to-add-java-awt-image-package-in-android)

**Impossible** (checked with escpos-coffee v.2.0.2)

