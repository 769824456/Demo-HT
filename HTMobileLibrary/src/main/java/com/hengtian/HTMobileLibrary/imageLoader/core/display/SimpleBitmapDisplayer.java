/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.hengtian.HTMobileLibrary.imageLoader.core.display;

import android.graphics.Bitmap;

import com.hengtian.HTMobileLibrary.imageLoader.core.assist.LoadedFrom;
import com.hengtian.HTMobileLibrary.imageLoader.core.imageaware.ImageAware;

/**
 * Just displays {@link Bitmap} in {@link com.hengtian.HTMobileLibrary.imageLoader.core.imageaware.ImageAware}
 *
 * @since 1.5.6
 */
public final class SimpleBitmapDisplayer implements BitmapDisplayer {
	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		imageAware.setImageBitmap(bitmap);
	}
}