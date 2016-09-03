/*
  Copyright (c) 2015 ControlsFX
  All rights reserved.
  <p>
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
  <p>
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

import javafx.beans.binding.NumberExpression;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import org.bitbucket.shaigem.rssb.store.ItemNameStore;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.math.BigInteger;

/**
 * A numeric text field that has autocomplete features from the item list. It only accepts numbers as a value.
 * However, normal text can be inputted to allow for suggestions from the item list.
 */
public class ItemListNumericField extends TextField {

    private final String validationRegex = "[\\dMmKk]+";

    private final Validator<String> validator = (control, string) -> {
        boolean condition = !string.matches(validationRegex);
        return ValidationResult.fromMessageIf(control,
                "Input must be a integer!",
                Severity.ERROR, condition);
    };

    private final NumericValidator<? extends Number> value;

    public ItemListNumericField(boolean itemList, Class<? extends Number> cls) {
        if (itemList)
            TextFields.bindAutoCompletion(this, ItemNameStore.getNamesMap().entrySet());
        registerValidator();

        if (cls == byte.class || cls == Byte.class || cls == short.class || cls == Short.class || cls == long.class || cls == Long.class ||
                cls == BigInteger.class) {
            value = new LongValidator(this);
        } else if (cls == int.class || cls == Integer.class) {
            value = new IntegerValidator(this);
        } else {
            value = new DoubleValidator(this);

        }

        textProperty().addListener((observable, oldValue, newValue) -> setText(newValue.split("=")[0]));

        textProperty().addListener(arg0 -> {
            String currentText = getText().split("=")[0];
            Number parsedNumber = null;
            try {
                parsedNumber = value.toNumber(currentText);
            } catch (NumberFormatException exception) {
                return;
            }
            value.setValue(parsedNumber);
        });

    }

    private void registerValidator() {
        ValidationSupport validationSupport = new ValidationSupport();
        validationSupport.setErrorDecorationEnabled(true);
        validationSupport.registerValidator(this, false, validator);
    }

    final ObservableValue<Number> valueProperty() {
        return value;
    }

    private interface NumericValidator<T extends Number> extends NumberExpression {
        void setValue(Number num);

        T toNumber(String s);

    }

    private static class DoubleValidator extends SimpleDoubleProperty implements NumericValidator<Double> {

        private ItemListNumericField field;

        DoubleValidator(ItemListNumericField field) {
            super(field, "value", 0.0); //$NON-NLS-1$
            this.field = field;
        }

        @Override
        protected void invalidated() {
            field.setText(Double.toString(get()));
        }

        @Override
        public Double toNumber(String s) {
            if (s == null || s.trim().isEmpty()) return 0d;
            String d = s.trim();
            if (d.endsWith("f") || d.endsWith("d") || d.endsWith("F") || d.endsWith("D")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                throw new NumberFormatException("There should be no alpha symbols"); //$NON-NLS-1$
            }
            Double doub = new Double(d);
            if (doub < 0) {
                doub *= -1;
            }
            return doub;
        }
    }

    private static class LongValidator extends SimpleLongProperty implements NumericValidator<Long> {

        private ItemListNumericField field;

        LongValidator(ItemListNumericField field) {
            super(field, "value", 0L); //$NON-NLS-1$
            this.field = field;
        }

        @Override
        protected void invalidated() {
            field.setText(Long.toString(get()));
        }

        @Override
        public Long toNumber(String s) {
            if (s == null || s.trim().isEmpty()) return 0L;
            String d = s.trim();
            Long l = new Long(d);
            if (l < 0) {
                l *= -1;
            }
            return l;
        }
    }


    private static class IntegerValidator extends SimpleIntegerProperty implements NumericValidator<Integer> {

        private ItemListNumericField field;

        IntegerValidator(ItemListNumericField field) {
            super(field, "value", 0); //$NON-NLS-1$
            this.field = field;
        }

        @Override
        protected void invalidated() {
            field.setText(Integer.toString(get()));
        }

        @Override
        public Integer toNumber(String s) {
            if (s == null || s.trim().isEmpty()) return 0;
            String d = s.trim();
            Integer l = new Integer(d);
            if (l < 0) {
                l *= -1;
            }
            return l;
        }
    }
}