/*
 * Copyright © 2017 Ivar Grimstad (ivar.grimstad@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mvcspec.ozark.binding;

import javax.enterprise.inject.Vetoed;
import javax.mvc.binding.BindingError;
import javax.mvc.binding.BindingResult;
import javax.mvc.binding.ValidationError;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation for {@link javax.mvc.binding.BindingResult} interface.
 *
 * @author Santiago Pericas-Geertsen
 * @author Christian Kaltepoth
 */
@Vetoed // produced by BindingResultManager
public class BindingResultImpl implements BindingResult {

    private Set<BindingError> errors = Collections.emptySet();

    private final Set<ValidationError> validationErrors = new LinkedHashSet<>();

    private boolean consumed;

    @Override
    public boolean isFailed() {
        this.consumed = true;
        return validationErrors.size() > 0 || errors.size() > 0;
    }

    @Override
    public List<String> getAllMessages() {
        this.consumed = true;
        final List<String> result = new ArrayList<>();
        errors.forEach(error -> result.add(error.getMessage()));
        validationErrors.forEach(violation -> result.add(violation.getMessage()));
        return result;
    }

    @Override
    public Set<BindingError> getAllBindingErrors() {
        this.consumed = true;
        return errors;
    }

    @Override
    public BindingError getBindingError(String param) {
        this.consumed = true;
        for (BindingError error : errors) {
            if (param.equals(error.getParamName())) {
                return error;
            }
        }
        return null;
    }

    @Override
    public Set<ValidationError> getAllValidationErrors() {
        this.consumed = true;
        return Collections.unmodifiableSet(validationErrors);
    }

    @Override
    public Set<ValidationError> getValidationErrors(String param) {
        this.consumed = true;
        return validationErrors.stream()
                .filter(ve -> Objects.equals(ve.getParamName(), param))
                .collect(Collectors.toSet());
    }

    @Override
    public ValidationError getValidationError(String param) {
        this.consumed = true;
        return validationErrors.stream()
                .filter(ve -> Objects.equals(ve.getParamName(), param))
                .findFirst().orElse(null);
    }

    public void addValidationErrors(Set<ValidationError> validationErrors) {
        this.validationErrors.addAll(validationErrors);
    }

    public void setErrors(Set<BindingError> errors) {
        this.errors = errors;
    }

    public boolean hasUnconsumedErrors() {
        return !consumed && (!errors.isEmpty() || !validationErrors.isEmpty());
    }

}
