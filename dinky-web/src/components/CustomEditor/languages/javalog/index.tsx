import {editor} from "monaco-editor";
import {languages} from "monaco-editor";

export  const JavaLog = () => {
    // Register a new language
    languages.register({ id: "java-log" });

// Register a tokens provider for the language
    languages.setMonarchTokensProvider("java-log", {
        tokenizer: {
            root: [
                [/\[error.*/, "custom-error"],
                [/\[notice.*/, "custom-notice"],
                [/.*/, "custom-info"],
                [/\[[a-zA-Z 0-9:]+\]/, "custom-date"],
            ],
        },
    });
}