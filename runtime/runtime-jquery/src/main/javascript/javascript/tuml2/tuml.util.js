function retrieveVertexId(url) {
    var urlId = url.match(/\/\d+/);
    if (urlId != null) {
        return urlId[0].match(/\d+/);
    } else {
        return urlId;
    }
}

function selectFieldValidator(property) {
    if (property.name == 'uri') {
    } else if (property.dataTypeEnum !== undefined) {
        if (property.dataTypeEnum == 'Date') {
            return new TumlSlick.Validators.TumlDateTime(property).validate;
        } else if (property.dataTypeEnum == 'Time') {
            return new TumlSlick.Validators.TumlDateTime(property).validate;
        } else if (property.dataTypeEnum == 'DateTime') {
            return new TumlSlick.Validators.TumlDateTime(property).validate;
        } else if (property.dataTypeEnum == 'InternationalPhoneNumber') {
        } else if (property.dataTypeEnum == 'LocalPhoneNumber') {
        } else if (property.dataTypeEnum == 'Email') {
            return new TumlSlick.Validators.TumlString(property).validate;
        } else if (property.dataTypeEnum == 'Video') {
        } else  if (property.dataTypeEnum == 'Audio') {
        } else if (property.dataTypeEnum == 'Image') {
        } else {
            alert('Unsupported dataType ' + property.dataTypeEnum);
        }
    } else if (!property.onePrimitive && !property.manyPrimitive && !property.composite) {
    } else if (property.name == 'id') {
    } else if (!property.manyPrimitive && property.fieldType == 'String') {
        return new TumlSlick.Validators.TumlString(property).validate;
    } else if (property.manyPrimitive && property.fieldType == 'String') {
        return new TumlSlick.Validators.TumlManyString(property).validate;
    } else if (!property.manyPrimitive && property.fieldType == 'Integer') {
        return new TumlSlick.Validators.TumlNumber(property).validate;
    } else if (property.manyPrimitive && property.fieldType == 'Integer') {
        return new TumlSlick.Validators.TumlManyNumber(property).validate;
    } else if (!property.manyPrimitive && property.fieldType == 'Long') {
        return new TumlSlick.Validators.TumlNumber(property).validate;
    } else if (property.manyPrimitive && property.fieldType == 'Long') {
        return new TumlSlick.Validators.TumlManyNumber(property).validate;
    } else if (!property.manyPrimitive && property.fieldType == 'Boolean') {
    } else {
    }
    return function() {
        return {
            valid: true,
            msg: null}
    };
}

function selectEditor(property) {
    if (property.name == 'uri') {
        return null;
    } else if (property.readOnly) {
        return null;
    } else if (property.dataTypeEnum !== undefined) {
        if (property.dataTypeEnum == 'Date') {
            return  Tuml.Slick.Editors.Date; 
        } else if (property.dataTypeEnum == 'Time') {
            return  Tuml.Slick.Editors.Time; 
        } else if (property.dataTypeEnum == 'DateTime') {
            return  Tuml.Slick.Editors.DateTime; 
        } else if (property.dataTypeEnum == 'InternationalPhoneNumber') {
            //TODO
            return Tuml.Slick.Editors.Text; 
        } else if (property.dataTypeEnum == 'LocalPhoneNumber') {
            //TODO
            return Tuml.Slick.Editors.Text; 
        } else if (property.dataTypeEnum == 'Email') {
            return Tuml.Slick.Editors.Text; 
        } else if (property.dataTypeEnum == 'Video') {
            return null; 
        } else  if (property.dataTypeEnum == 'Audio') {
            return null; 
        } else if (property.dataTypeEnum == 'Image') {
            return null; 
        } else {
            alert('Unsupported dataType ' + property.dataTypeEnum);
        }
    } else if (property.oneEnumeration) {
        return  Tuml.Slick.Editors.SelectEnumerationCellEditor; 
    } else if (property.manyPrimitive) {
        return  Tuml.Slick.Editors.ManyPrimitiveEditor; 
    } else if (!property.onePrimitive && !property.manyPrimitive && !property.composite && property.oneToOne) {
        return  Tuml.Slick.Editors.SelectOneToOneCellEditor; 
    } else if (!property.onePrimitive && !property.manyPrimitive && !property.composite && property.manyToOne) {
        return  Tuml.Slick.Editors.SelectManyToOneCellEditor; 
    } else if (property.name == 'id') {
        return null;
    } else if (property.fieldType == 'String') {
        return Tuml.Slick.Editors.Text; 
    } else if (property.fieldType == 'Integer') {
        return Tuml.Slick.Editors.Integer;
    } else if (property.fieldType == 'Long') {
        return Slick.Editors.Integer;
    } else if (property.fieldType == 'Boolean') {
        return Tuml.Slick.Editors.Checkbox;
    } else {
        return  Slick.Editors.Text; 
    }

}