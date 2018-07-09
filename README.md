# VAqua and JNR library


Source from https://violetlib.org/vaqua/overview.html

Author: Alan Snyder (https://github.com/cbfiddle)


# Custom changes

## Support JetBrains JDK 8 (which contains JDK 9 hidpi changes)

 * Default hack for transparent title bar **Aqua.windowStyle**=**transparentTitleBar** don't at JetBrains JDK - need use their hack **jetbrains.awt.transparentTitleBarAppearance**=**true**

## List UI support vibrant effects

| Before        | After        |
| ------------- |:-------------:|
| ![](/images/listui-no-vibrant.png)    | ![](/images/listui-vibrant.png) |

## Transparent title bar allow zero top margin

| Before        | After        |
| ------------- |:-------------:|
| ![](/images/transparentTitleBar-no-top-margin.png)    | ![](/images/transparentTitleBar-top-margin-zero.png) |
