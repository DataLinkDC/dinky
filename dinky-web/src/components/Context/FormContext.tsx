import React from 'react';

export interface FormContextValue {
    resetForm: () => void;
}

export const FormContext = React.createContext<FormContextValue>({
    resetForm: () => {},
});
