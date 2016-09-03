/*
  Copyright (c) 2015 ControlsFX
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met:
      * Redistributions of source code must retain the above copyright
  notice, this list of conditions and the following disclaimer.
      * Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
      * Neither the name of ControlsFX, any associated website, nor the
  names of its contributors may be used to endorse or promote products
  derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.bitbucket.shaigem.rssb.fx.controlsfx;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextInputControl;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.AbstractPropertyEditor;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.PropertyEditor;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class CustomPropertyEditorFactory extends DefaultPropertyEditorFactory {

    @Override
    public PropertyEditor<?> call(PropertySheet.Item item) {
       // Class<?> type = item.getType();

        if (item.getName().equals("currency")) { // must be higher than isNumber
            return createNumericEditor(true, item);
        }

        return super.call(item);
    }

    private static PropertyEditor<?> createNumericEditor(boolean itemList, PropertySheet.Item property) {

        return new AbstractPropertyEditor<Number, ItemListNumericField>(property, new ItemListNumericField(itemList, (Class<? extends Number>) property.getType())) {

            private Class<? extends Number> sourceClass = (Class<? extends Number>) property.getType(); //Double.class;

            {
                enableAutoSelectAll(getEditor());
            }

            @Override
            protected ObservableValue<Number> getObservableValue() {
                return getEditor().valueProperty();
            }

            @Override
            public Number getValue() {
                try {
                    return sourceClass.getConstructor(String.class).newInstance(getEditor().getText());
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                        | NoSuchMethodException | SecurityException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void setValue(Number value) {
                sourceClass = value.getClass();
                getEditor().setText(value.toString());
            }

        };
    }


    private static void enableAutoSelectAll(final TextInputControl control) {
        control.focusedProperty().addListener((ObservableValue<? extends Boolean> o, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                Platform.runLater(control::selectAll);
            }
        });
    }

    private static Class<?>[] numericTypes = new Class[]{
            byte.class, Byte.class,
            short.class, Short.class,
            int.class, Integer.class,
            long.class, Long.class,
            float.class, Float.class,
            double.class, Double.class,
            BigInteger.class, BigDecimal.class
    };

    // there should be better ways to do this
    private static boolean isNumber(Class<?> type) {
        if (type == null) return false;
        for (Class<?> cls : numericTypes) {
            if (type == cls) return true;
        }
        return false;
    }
}
