/***
 * Contains basic TumlSlickGrid validators.
 * @module Validators
 * @namespace TumlSlick
 */

(function ($) {
    // register namespace
    $.extend(true, window, {
        "TumlSlick": {
            "Validators": {
                "TumlObject": TumlObjectValidator,
                "TumlString": TumlStringValidator,
                "TumlDateTime": TumlDateTimeValidator,
                "RangeLength": RangeLengthValidator,
                "MaxLength": MaxLengthValidator,
                "MinLength": MinLengthValidator,
                "Url": UrlValidator,
                "Email": EmailValidator,
                "QuartzCron": QuartzCronValidator,
                "Host": HostValidator,
                "DateTime": DateTimeValidator,
                "Date": DateValidator,
                "Time": TimeValidator,
                "TumlManyEnumerationValidator": TumlManyEnumerationValidator,
                "TumlNumber": TumlNumberValidator,
                "TumlBoolean": TumlBooleanValidator,
                "TumlManyNumber": TumlManyNumberValidator,
                "TumlManyString": TumlManyStringValidator,
                "TumlManyBoolean": TumlManyBooleanValidator,
                "RangeInteger": RangeIntegerValidator,
                "MaxInteger": MaxIntegerValidator,
                "MinInteger": MinIntegerValidator,
                "RangeUnlimitedNatural": RangeUnlimitedNaturalValidator,
                "MaxUnlimitedNatural": MaxUnlimitedNaturalValidator,
                "MinUnlimitedNatural": MinUnlimitedNaturalValidator,
                "RangeReal": RangeRealValidator,
                "MaxReal": MaxRealValidator,
                "MinReal": MinRealValidator,
                "Required": RequiredValidator,
                "Number": NumberValidator,
                "TumlManyDateValidator": TumlManyDateValidator,
                "TumlManyTimeValidator": TumlManyTimeValidator,
                "TumlManyDateTimeValidator": TumlManyDateTimeValidator
            }
        }
    });

    function TumlObjectValidator(property) {
        //Public api
        $.extend(this, {
            "TumlObjectValidator": "1.0.0",
            "validate": validate
        });

        function validate(value) {
            var result = TumlSlick.Validators.Required(property, value);
            if (!result.valid) {
                return result;
            }
            return {
                valid: true,
                msg: null
            };
        }
    };

    function TumlDateTimeValidator(property) {
        //Public api
        $.extend(this, {
            "TumlDateTimeValidator": "1.0.0",
            "validate": validate
        });

        function validate(value) {
            var result = TumlSlick.Validators.Required(property, value);
            if (!result.valid) {
                return result;
            }
            if (value !== undefined && value !== null && value !== '') {
                if (property.validations !== null) {
                    if (property.validations.date !== undefined) {
                        result = TumlSlick.Validators.Date(property, value).validate();
                    }
                    if (!result.valid) {
                        return result;
                    }
                    if (property.validations.time !== undefined) {
                        result = TumlSlick.Validators.Time(property, value);
                    }
                    if (!result.valid) {
                        return result;
                    }
                    if (property.validations.dateTime !== undefined) {
                        result = TumlSlick.Validators.DateTime(property, value);
                    }
                    if (!result.valid) {
                        return result;
                    }
                }
            }
            return {
                valid: true,
                msg: null
            };
        }
    };
    function TumlStringValidator(property) {
        //Public api
        $.extend(this, {
            "TumlStringValidator": "1.0.0",
            "validate": validate
        });

        function validate(value) {
            var result = TumlSlick.Validators.Required(property, value);
            if (!result.valid) {
                return result;
            }
            if (value !== undefined && value !== null && value !== '') {
                if (property.validations !== null) {
                    if (property.validations.rangeLength !== undefined) {
                        result = TumlSlick.Validators.RangeLength(property, value);
                    }
                    if (!result.valid) {
                        return result;
                    }
                    if (property.validations.maxLength !== undefined) {
                        result = TumlSlick.Validators.MaxLength(property, value);
                    }
                    if (!result.valid) {
                        return result;
                    }
                    if (property.validations.minLength !== undefined) {
                        result = TumlSlick.Validators.MinLength(property, value);
                    }
                    if (!result.valid) {
                        return result;
                    }
                    if (property.validations.url !== undefined) {
                        result = TumlSlick.Validators.Url(property, value);
                    }
                    if (!result.valid) {
                        return result;
                    }
                    if (property.validations.email !== undefined) {
                        result = TumlSlick.Validators.Email(property, value);
                    }
                    if (property.validations.quartzCron !== undefined) {
                        result = TumlSlick.Validators.QuartzCron(property, value);
                    }
                    if (property.validations.host !== undefined) {
                        result = TumlSlick.Validators.Host(property, value);
                    }
                    if (!result.valid) {
                        return result;
                    }
                }
            }
            return {
                valid: true,
                msg: null
            };
        }
    };

    function TumlManyStringValidator(property) {

        //Public api
        $.extend(this, {
            "TumlManyStringValidator": "1.0.0",
            "validate": validate
        });

        function validateSingleProperty(tmp) {
            return new TumlSlick.Validators.TumlString(property).validate(tmp);
        }

        function validate(currentValues, valueToAdd) {
            return manyValidate(property, validateSingleProperty, currentValues, valueToAdd)
        }

    };

    function TumlManyEnumerationValidator(property) {

        //Public api
        $.extend(this, {
            "TumlManyEnumerationValidator": "1.0.0",
            "validate": validate
        });

        function validateSingleProperty(tmp) {
            return new TumlSlick.Validators.TumlString(property).validate(tmp);
        }

        function validate(currentValues, valueToAdd) {
            return manyValidate(property, validateSingleProperty, currentValues, valueToAdd)
        }

    };

    function TumlManyDateValidator(property) {

        //Public api
        $.extend(this, {
            "TumlManyDateValidator": "1.0.0",
            "validate": validate
        });

        function validateSingleProperty(tmp) {
            return new TumlSlick.Validators.Date(property).validate(tmp);
        }

        function validate(currentValues, valueToAdd) {
            return manyValidate(property, validateSingleProperty, currentValues, valueToAdd)
        }

    };

    function TumlManyTimeValidator(property) {

        //Public api
        $.extend(this, {
            "TumlManyTimeValidator": "1.0.0",
            "validate": validate
        });

        function validateSingleProperty(tmp) {
            return new TumlSlick.Validators.Time(property).validate(tmp);
        }

        function validate(currentValues, valueToAdd) {
            return manyValidate(property, validateSingleProperty, currentValues, valueToAdd)
        }

    };

    function TumlManyDateTimeValidator(property) {

        //Public api
        $.extend(this, {
            "TumlManyDateTimeValidator": "1.0.0",
            "validate": validate
        });

        function validateSingleProperty(tmp) {
            return new TumlSlick.Validators.DateTime(property).validate(tmp);
        }

        function validate(currentValues, valueToAdd) {
            return manyValidate(property, validateSingleProperty, currentValues, valueToAdd)
        }

    };

    function TumlManyBooleanValidator(property) {

        //Public api
        $.extend(this, {
            "TumlManyBooleanValidator": "1.0.0",
            "validate": validate
        });

        function validateSingleProperty(tmp) {
            return new TumlSlick.Validators.TumlBoolean(property).validate(tmp);
        }

        function validate(currentValues, valueToAdd) {
            if (valueToAdd !== undefined) {

                var result = validateSingleProperty(valueToAdd);
                if (!result.valid) {
                    throw 'Value must be "true" or "t" or "1" or "false" or "f" or "0"';
                }

//                if (!(valueToAdd === 'true' || valueToAdd === 't' || valueToAdd === '1' || valueToAdd === 'false' || valueToAdd === 'f' || valueToAdd === '0')) {
//                    throw 'Value must be "true" or "t" or "1" or "false" or "f" or "0"';
//                }
                if (valueToAdd === 'true' || valueToAdd === 't' || valueToAdd === '1' || valueToAdd) {
                    return manyValidate(property, validateSingleProperty, currentValues, true)
                } else {
                    return manyValidate(property, validateSingleProperty, currentValues, false)
                }
            } else {
                return manyValidate(property, validateSingleProperty, currentValues, valueToAdd)
            }
        }

    };

    function TumlManyNumberValidator(property) {

        //Public api
        $.extend(this, {
            "TumlManyNumberValidator": "1.0.0",
            "validate": validate
        });

        function validateSingleProperty(tmp) {
            return new TumlSlick.Validators.TumlNumber(property).validate(tmp);
        }

        function validate(currentValues, valueToAdd) {
            return manyValidate(property, validateSingleProperty, currentValues, valueToAdd)
        }

    };

    function manyValidate(property, validateSingleProperty, currentValues, valueToAdd) {
        //Validate that the property is not already present for a unique set.
        //The value itself has already been validated to be correct.
        if (currentValues instanceof Array && valueToAdd !== undefined && property.unique) {
            for (var i = 0; i < currentValues.length; i++) {
                var tmp = currentValues[i];
                if (tmp == valueToAdd) {
                    return {
                        valid: false,
                        msg: 'The list must be unique, ' + valueToAdd + ' is already present.'
                    };
                }
            }
            return {
                valid: true,
                msg: null
            };
        } else {
            //Validate the current values, this gets called for new items to post
            var result = {
                valid: false,
                msg: null
            };
            //Nothing to validate but the property is required
            if (property.lower > 0 && (currentValues == undefined || currentValues == null || currentValues.length == 0)) {
                return {
                    valid: false,
                    msg: property.name + " is a required field!"
                };
            }
            //Nothing to validate and the property is not required
            if (property.lower == 0 && (currentValues == undefined || currentValues == null || currentValues.length == 0)) {
                return {
                    valid: true,
                    msg: null
                };
            }
            //Validate each property
            for (var i = 0; i < currentValues.length; i++) {
                var tmp = currentValues[i];
                result = validateSingleProperty(tmp);
                if (!result.valid) {
                    return result;
                }
            }
            return result;
        }
        return {
            valid: false,
            msg: null
        };
    }


    function TumlBooleanValidator(property) {
        //Public api
        $.extend(this, {
            "TumlBooleanValidator": "1.0.0",
            "validate": validate
        });

        function validate(value) {
            var result = TumlSlick.Validators.Required(property, value);
            if (!result.valid) {
                return result;
            }
            if (value !== undefined && value !== null && value !== '' && value !== true && value !== false) {
                if (!(value === 'true' || value === 't' || value === '1' || value === 'false' || value === 'f' || value === '0')) {
                    return {valid: false, msg: 'Value must be "true" or "t" or "1" or "false" or "f" or "0"'};
                }
            }
            return {
                valid: true,
                msg: null
            };
        }
    };

    function TumlNumberValidator(property) {
        //Public api
        $.extend(this, {
            "TumlNumberValidator": "1.0.0",
            "validate": validate
        });
        function validate(value) {
            var result = TumlSlick.Validators.Required(property, value);
            if (!result.valid) {
                return result;
            }
            if (value !== undefined && value !== null && value !== '') {
                result = TumlSlick.Validators.Number(property, value);
                if (!result.valid) {
                    return result;
                }
                if (property.fieldType === 'Integer' && parseFloat(value) % 1 !== 0) {
                    return {
                        valid: false,
                        msg: property.name + " is an is not a valid Integer."
                    }
                }
                if (property.fieldType === 'Long' && parseFloat(value) % 1 !== 0) {
                    return {
                        valid: false,
                        msg: property.name + " is an is not a valid Long."
                    }
                }
                if (property.fieldType === 'UnlimitedNatural' && value < 0) {
                    return {
                        valid: false,
                        msg: property.name + " is an UnlimitedNatural, it must be greater or equal to 0."
                    }
                }
                if (property.validations !== null) {
                    if (property.fieldType === 'Integer' || property.fieldType === 'Long') {
                        if (property.validations.range !== undefined) {
                            result = TumlSlick.Validators.RangeInteger(property, value);
                        }
                        if (!result.valid) {
                            return result;
                        }
                        if (property.validations.max !== undefined) {
                            result = TumlSlick.Validators.MaxInteger(property, value);
                        }
                        if (!result.valid) {
                            return result;
                        }
                        if (property.validations.min !== undefined) {
                            result = TumlSlick.Validators.MinInteger(property, value);
                        }
                    } else if (property.fieldType === 'UnlimitedNatural') {
                        if (property.validations.range !== undefined) {
                            result = TumlSlick.Validators.RangeUnlimitedNatural(property, value);
                        }
                        if (!result.valid) {
                            return result;
                        }
                        if (property.validations.max !== undefined) {
                            result = TumlSlick.Validators.MaxUnlimitedNatural(property, value);
                        }
                        if (!result.valid) {
                            return result;
                        }
                        if (property.validations.min !== undefined) {
                            result = TumlSlick.Validators.MinUnlimitedNatural(property, value);
                        }
                    } else if (property.fieldType === 'Real' || property.fieldType === 'Double' || property.fieldType === 'Float') {
                        if (property.validations.range !== undefined) {
                            result = TumlSlick.Validators.RangeReal(property, value);
                        }
                        if (!result.valid) {
                            return result;
                        }
                        if (property.validations.max !== undefined) {
                            result = TumlSlick.Validators.MaxReal(property, value);
                        }
                        if (!result.valid) {
                            return result;
                        }
                        if (property.validations.min !== undefined) {
                            result = TumlSlick.Validators.MinReal(property, value);
                        }
                    }
                    if (!result.valid) {
                        return result;
                    }
                }
            }
            return {
                valid: true,
                msg: null
            };
        }
    };

    function NumberValidator(property, value) {
        if (value === undefined || value === null || value === '' || isNaN(value)) {
            return {
                valid: false,
                msg: "Please enter a number!"
            };
        } else {
            return {valid: true};
        }
    }

    function RangeLengthValidator(property, value) {
        if (value !== undefined && (value.length < property.validations.rangeLength.min || value.length > property.validations.rangeLength.max)) {
            return {
                valid: false,
                msg: property.name + "'s value's length must be between " + property.validations.rangeLength.min + " and " + property.validations.rangeLength.max
            }
        } else {
            return {valid: true};
        }
    }

    function MaxLengthValidator(property, value) {
        if (value !== undefined && value.length > property.validations.maxLength) {
            return {
                valid: false,
                msg: property.name + "'s value's length must be smaller than or equal to " + property.validations.maxLength
            }
        } else {
            return {valid: true};
        }
    }

    function MinLengthValidator(property, value) {
        if (value !== undefined && value.length < property.validations.minLength) {
            return {
                valid: false,
                msg: property.name + "'s value's length must be greater than or equal to " + property.validations.minLength
            }
        } else {
            return {valid: true};
        }
    }

    function RangeIntegerValidator(property, value) {
        var intValue = parseInt(value, 10);
        if (intValue < property.validations.range.min || intValue > property.validations.range.max) {
            return {
                valid: false,
                msg: property.name + "'s value must be between " + property.validations.range.min + " and " + property.validations.range.max + " (including)"
            }
        } else {
            return {valid: true};
        }
    }

    function MaxIntegerValidator(property, value) {
        if (isNaN(value)) {
            return {
                valid: false,
                msg: "Value must be anumber"
            }
        } else {
            var intValue = parseInt(value, 10);
            if (intValue > property.validations.max) {
                return {
                    valid: false,
                    msg: property.name + "'s value must be smaller than or equal to " + property.validations.max
                }
            } else {
                return {valid: true};
            }
        }
    }

    function MinIntegerValidator(property, value) {
        if (isNaN(value)) {
            return {
                valid: false,
                msg: "Value must be a number"
            }
        } else {
            var intValue = parseInt(value, 10);
            if (intValue < property.validations.min) {
                return {
                    valid: false,
                    msg: property.name + "'s value must be greater than or equal to " + property.validations.min
                }
            } else {
                return {valid: true};
            }
        }
    }

    //UnlimitedNatural may not be < 0
    function RangeUnlimitedNaturalValidator(property, value) {
        var intValue = parseInt(value, 10);
        if (intValue < 0 ||intValue < property.validations.range.min || intValue > property.validations.range.max) {
            return {
                valid: false,
                msg: "A UnlimitedNatural must be greater than or equal to zero. " + property.name + "'s value must be between " + property.validations.range.min + " and " + property.validations.range.max + " (including)"
            }
        } else {
            return {valid: true};
        }
    }

    //UnlimitedNatural may not be < 0
    function MaxUnlimitedNaturalValidator(property, value) {
        if (isNaN(value)) {
            return {
                valid: false,
                msg: "Value must be a number"
            }
        } else {
            var intValue = parseInt(value, 10);
            if (intValue < 0 || intValue > property.validations.max) {
                return {
                    valid: false,
                    msg: "A UnlimitedNatural must be greater or equal to 0. " + property.name + " must be greater or equal to " + property.validations.max
                }
            } else {
                return {valid: true};
            }
        }
    }

    //UnlimitedNatural may not be < 0
    function MinUnlimitedNaturalValidator(property, value) {
        if (isNaN(value)) {
            return {
                valid: false,
                msg: "Value must be a number"
            }
        } else {
            var intValue = parseInt(value, 10);
            if (intValue < 0 || intValue < property.validations.min) {
                return {
                    valid: false,
                    msg: "A UnlimitedNatural must be greater than or equal to zero. " + property.name + "'s value must be greater than or equal to " + property.validations.min
                }
            } else {
                return {valid: true};
            }
        }
    }

    function RangeRealValidator(property, value) {
        var floatValue = parseFloat(value, 10);
        if (floatValue < property.validations.range.min || floatValue > property.validations.range.max) {
            return {
                valid: false,
                msg: property.name + "'s value must be between " + property.validations.range.min + " and " + property.validations.range.max + " (including)"
            }
        } else {
            return {valid: true};
        }
    }

    function MaxRealValidator(property, value) {
        if (isNaN(value)) {
            return {
                valid: false,
                msg: "Value must be anumber"
            }
        } else {
            var floatValue = parseFloat(value, 10);
            if (floatValue > property.validations.max) {
                return {
                    valid: false,
                    msg: property.name + "'s value must be smaller than or equal to " + property.validations.max
                }
            } else {
                return {valid: true};
            }
        }
    }

    function MinRealValidator(property, value) {
        if (isNaN(value)) {
            return {
                valid: false,
                msg: "Value must be a number"
            }
        } else {
            var floatValue = parseFloat(value, 10);
            if (floatValue < property.validations.min) {
                return {
                    valid: false,
                    msg: property.name + "'s value must be greater than or equal to " + property.validations.min
                }
            } else {
                return {valid: true};
            }
        }
    }



    function UrlValidator(property, value) {
        return {valid: true};
    }

    function EmailValidator(property, value) {
        var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        if (!re.test(value)) {
            return {
                valid: false,
                msg: "Value is not a valid email address!"
            }
        } else {
            return {valid: true};
        }
    }

    function QuartzCronValidator(property, value) {
        return {valid: true};
    }

    function HostValidator(property, value) {
        var re = /((^\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\s*$)|(^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$))|(^\s*((?=.{1,255}$)(?=.*[A-Za-z].*)[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?(?:\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?)*)\s*$)/;
        if (!re.test(value)) {
            return {
                valid: false,
                msg: "Value is not a valid host name!"
            }
        } else {
            return {valid: true};
        }
    }

    function DateTimeValidator(property, value) {
        //Public api
        $.extend(this, {
            "DateTimeValidator": "1.0.0",
            "validate": validate
        });

        function validate(value) {
            var result = TumlSlick.Validators.Required(property, value);
            if (!result.valid) {
                return result;
            }
            if (property.lower === 0 && value === '') {
                return {valid: true};
            }
            var m = moment(value);
            if (m.isValid()) {
                return {valid: true};
            } else {
                return {valid: false, msg: value + "'s format is incorrect, the format is 'yy-mm-dd HH:mm:ss'"}
            }
        }

    }

    function DateValidator(property, value) {

        //Public api
        $.extend(this, {
            "DateValidator": "1.0.0",
            "validate": validate
        });

        function validate(value) {
            var result = TumlSlick.Validators.Required(property, value);
            if (!result.valid) {
                return result;
            }
            if (property.lower === 0 && value === '') {
                return {valid: true};
            }
            var m = moment(value);
            if (m.isValid()) {
                return {valid: true};
            } else {
                return {valid: false, msg: value + "'s format is incorrect, the format is 'yy-mm-dd'"}
            }
        }
    }

    function TimeValidator(property, value) {

        //Public api
        $.extend(this, {
            "TimeValidator": "1.0.0",
            "validate": validate
        });

        function validate(value) {
            var result = TumlSlick.Validators.Required(property, value);
            if (!result.valid) {
                return result;
            }
            if (property.lower === 0 && value === '') {
                return {valid: true};
            }

            var m = moment(value);
            if (m.isValid()) {
                return {valid: true};
            } else {
                return {valid: false, msg: value + "'s format is incorrect, the format is 'HH:mm'"}
            }

        }

    }

    function RequiredValidator(property, value) {
        if (property.lower > 0 && value !== false && value !== true && (value === '' || value === undefined || value === null)) {
            return {
                valid: false,
                msg: property.name + " is a required field!",
                present: false
            };
        } else {
            return {valid: true, present: true};
        }
    }
})(jQuery);
