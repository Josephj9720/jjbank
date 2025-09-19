const fullNameRegex = /^[a-zA-Z\u00C0-\u017F]+([ \-']{1,2}[a-zA-Z\u00C0-\u017F]+)*$/;
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; //need to understand this

export const validateRegister = (name, email, password) => {
  const isNameValid = validateFullName(name);
  const isEmailValid = validateEmail(email);
  const isPasswordValid = validatePassword(password);

  const isValid = isNameValid.result && isEmailValid.result && isPasswordValid.result;
  return {
    isValid : isValid,
    nameErrorMessage : isNameValid.message,
    emailErrorMessage : isEmailValid.message,
    passwordErrorMessage : isPasswordValid.message,
  };

}

export const validateLogin = (email, password) => {
  const isEmailValid = validateEmail(email);
  const isPasswordValid = validatePassword(password);

  const isValid = isEmailValid.result && isPasswordValid.result;
  return {
    isValid : isValid,
    emailErrorMessage : isEmailValid.message,
    passwordErrorMessage : isPasswordValid.message,
  };
}

const validateFullName = (name) => {
  if(!name) return responseObject(false, "Full Name is empty.");
  return fullNameRegex.test(name) 
    ? 
      responseObject(true, "") 
    : 
      responseObject(false, "Name must not contain numbers, leading spaces or end with hyphen.");
}

const validateEmail = (email) => {
  if(!email) return responseObject(false, "Email is empty.");
  return emailRegex.test(email) 
    ? 
      responseObject(true, "") 
    : 
      responseObject(false, "Email is not valid.");
}

//very simple for now
const validatePassword = (password) => {
  if(!password) return responseObject(false, "Password is empty.");
  return password.length >= 8
    ? 
      responseObject(true, "") 
    : 
      responseObject(false ,"Password must have at least 8 characters.");
}

const responseObject = (result, message) => {
  return {
    result : result,
    message : message,
  };
}