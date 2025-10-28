const fullNameRegex = /^[a-zA-Z\u00C0-\u017F]+([ \-']{1,2}[a-zA-Z\u00C0-\u017F]+)*$/;
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; //need to understand this
const accessCardRegex = /^[A-Z]\d{4}[A-Z]\d{4}[A-Z]\d{4}[A-Z]$/i; //i = case insensitive

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

export const validateLogin = (identifier, password) => {
  const isIdentifierValid = validateIdentifier(identifier);
  const isPasswordValid = validatePassword(password);

  const isValid = isIdentifierValid.result && isPasswordValid.result;
  return {
    isValid : isValid,
    identifierErrorMessage : isIdentifierValid.message,
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

const validateAccessCard = (cardNumber) => {
  if(!cardNumber) return responseObject(false, "Access card is empty.");
  return accessCardRegex.test(cardNumber)
    ?
      responseObject(true, "")
    :
      responseObject(false, "Access card is not valid.")
}

const validateIdentifier = (identifier) => {
  if(!identifier) return responseObject(false, "Please enter your email or access card number.");
  const isEmailValid = validateEmail(identifier);
  if(isEmailValid.result == false){
    const isAccessCardValid = validateAccessCard(identifier);
    if(isAccessCardValid.result == false) {
      return responseObject(false, "Your login identifier is not valid. Please verify and retry.");
    }
    return isAccessCardValid;
  }
  return isEmailValid;

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