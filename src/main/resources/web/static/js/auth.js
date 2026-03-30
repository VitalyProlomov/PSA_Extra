// Password Toggle Functionality
document.addEventListener('DOMContentLoaded', () => {
    // ========== Password Toggle (Login & Register) ==========
    const togglePassword = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');
    const toggleConfirmPassword = document.getElementById('toggleConfirmPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const submitBtn = document.getElementById('submitBtn');

    // Toggle password visibility
    if (togglePassword && passwordInput) {
        togglePassword.addEventListener('click', () => {
            togglePasswordVisibility(passwordInput, togglePassword);
        });
    }

    if (toggleConfirmPassword && confirmPasswordInput) {
        toggleConfirmPassword.addEventListener('click', () => {
            togglePasswordVisibility(confirmPasswordInput, toggleConfirmPassword);
        });
    }

    // ========== Registration Form Validation ==========
    if (registerForm) {
        const usernameInput = document.getElementById('username');
        const emailInput = document.getElementById('email');
        const termsCheckbox = document.getElementById('agreeTerms');
        const termsError = document.getElementById('termsError');

        // Real-time validation
        if (usernameInput) {
            usernameInput.addEventListener('input', () => {
                validateUsername(usernameInput);
                validatePasswordMatch(passwordInput, confirmPasswordInput);
            });
        }

        if (emailInput) {
            emailInput.addEventListener('input', () => {
                validateEmail(emailInput);
            });
        }

        if (passwordInput) {
            passwordInput.addEventListener('input', () => {
                validatePasswordStrength(passwordInput);
                validatePasswordMatch(passwordInput, confirmPasswordInput);
            });
        }

        if (confirmPasswordInput) {
            confirmPasswordInput.addEventListener('input', () => {
                validatePasswordMatch(passwordInput, confirmPasswordInput);
            });
        }

        if (termsCheckbox) {
            termsCheckbox.addEventListener('change', () => {
                if (termsCheckbox.checked && termsError) {
                    termsError.style.display = 'none';
                }
            });
        }

        // Form submission
        registerForm.addEventListener('submit', (e) => {
            let isValid = true;

            // Validate all fields
            if (usernameInput) isValid = validateUsername(usernameInput) && isValid;
            if (emailInput) isValid = validateEmail(emailInput) && isValid;
            if (passwordInput) isValid = validatePasswordStrength(passwordInput) && isValid;
            if (confirmPasswordInput) isValid = validatePasswordMatch(passwordInput, confirmPasswordInput) && isValid;

            // Check terms agreement
            if (termsCheckbox && !termsCheckbox.checked) {
                e.preventDefault();
                if (termsError) termsError.style.display = 'block';
                isValid = false;
            }

            if (!isValid) {
                e.preventDefault();
                showNotification('Please fix the errors above', 'error');
                return;
            }

            // Show loading state
            if (submitBtn) {
                submitBtn.classList.add('btn-loading');
                submitBtn.disabled = true;
            }

            // Form will submit normally to Spring controller
        });
    }

    // ========== Login Form ==========
    if (loginForm && submitBtn) {
        loginForm.addEventListener('submit', (e) => {
            const username = document.getElementById('username')?.value;
            const password = document.getElementById('password')?.value;

            if (!username || !password) {
                e.preventDefault();
                showNotification('Please fill in all fields', 'error');
                return;
            }

            // Show loading state
            submitBtn.classList.add('btn-loading');
            submitBtn.disabled = true;
        });
    }

    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.animation = 'slideOut 0.3s ease-out forwards';
            setTimeout(() => alert.remove(), 300);
        }, 5000);
    });
});

// ========== Helper Functions ==========

function togglePasswordVisibility(input, toggleBtn) {
    const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
    input.setAttribute('type', type);

    const eyeIcon = toggleBtn.querySelector('.eye-icon');
    if (type === 'text') {
        eyeIcon.innerHTML = `
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"/>
        `;
    } else {
        eyeIcon.innerHTML = `
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
        `;
    }
}

function validateUsername(input) {
    const value = input.value;
    const lengthHint = document.getElementById('usernameLength');
    const formatHint = document.getElementById('usernameFormat');
    const formGroup = input.closest('.form-group');

    let isValid = true;

    // Length check (3-50 characters)
    if (lengthHint) {
        if (value.length >= 3 && value.length <= 50) {
            lengthHint.classList.add('valid');
            lengthHint.classList.remove('invalid');
        } else {
            lengthHint.classList.add('invalid');
            lengthHint.classList.remove('valid');
            isValid = false;
        }
    }

    // Format check (letters, numbers, underscores only)
    if (formatHint) {
        const formatRegex = /^[a-zA-Z0-9_]+$/;
        if (formatRegex.test(value)) {
            formatHint.classList.add('valid');
            formatHint.classList.remove('invalid');
        } else {
            formatHint.classList.add('invalid');
            formatHint.classList.remove('valid');
            isValid = false;
        }
    }

    // Update form group styling
    if (formGroup) {
        if (isValid && value.length > 0) {
            formGroup.classList.add('has-success');
            formGroup.classList.remove('has-error');
        } else if (value.length > 0) {
            formGroup.classList.add('has-error');
            formGroup.classList.remove('has-success');
        } else {
            formGroup.classList.remove('has-success', 'has-error');
        }
    }

    return isValid;
}

function validateEmail(input) {
    const value = input.value;
    const formatHint = document.getElementById('emailFormat');
    const formGroup = input.closest('.form-group');

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    let isValid = emailRegex.test(value);

    if (formatHint) {
        if (isValid) {
            formatHint.classList.add('valid');
            formatHint.classList.remove('invalid');
        } else {
            formatHint.classList.add('invalid');
            formatHint.classList.remove('valid');
        }
    }

    if (formGroup) {
        if (isValid && value.length > 0) {
            formGroup.classList.add('has-success');
            formGroup.classList.remove('has-error');
        } else if (value.length > 0) {
            formGroup.classList.add('has-error');
            formGroup.classList.remove('has-success');
        } else {
            formGroup.classList.remove('has-success', 'has-error');
        }
    }

    return isValid;
}

function validatePasswordStrength(input) {
    const value = input.value;
    const lengthHint = document.getElementById('passwordLength');
    const upperHint = document.getElementById('passwordUpper');
    const lowerHint = document.getElementById('passwordLower');
    const numberHint = document.getElementById('passwordNumber');
    const formGroup = input.closest('.form-group');

    let score = 0;

    // Length check (at least 8 characters)
    if (lengthHint) {
        if (value.length >= 8) {
            lengthHint.classList.add('valid');
            lengthHint.classList.remove('invalid');
            score++;
        } else {
            lengthHint.classList.add('invalid');
            lengthHint.classList.remove('valid');
        }
    }

    // Uppercase check
    if (upperHint) {
        if (/[A-Z]/.test(value)) {
            upperHint.classList.add('valid');
            upperHint.classList.remove('invalid');
            score++;
        } else {
            upperHint.classList.add('invalid');
            upperHint.classList.remove('valid');
        }
    }

    // Lowercase check
    if (lowerHint) {
        if (/[a-z]/.test(value)) {
            lowerHint.classList.add('valid');
            lowerHint.classList.remove('invalid');
            score++;
        } else {
            lowerHint.classList.add('invalid');
            lowerHint.classList.remove('valid');
        }
    }

    // Number check
    if (numberHint) {
        if (/[0-9]/.test(value)) {
            numberHint.classList.add('valid');
            numberHint.classList.remove('invalid');
            score++;
        } else {
            numberHint.classList.add('invalid');
            numberHint.classList.remove('valid');
        }
    }

    // Password strength bar (if exists)
    const strengthBar = document.querySelector('.password-strength-bar');
    const strengthText = document.querySelector('.password-strength-text');

    if (strengthBar) {
        strengthBar.className = 'password-strength-bar';
        if (score === 4) {
            strengthBar.classList.add('strong');
            if (strengthText) strengthText.textContent = 'Strong';
        } else if (score === 3) {
            strengthBar.classList.add('good');
            if (strengthText) strengthText.textContent = 'Good';
        } else if (score === 2) {
            strengthBar.classList.add('fair');
            if (strengthText) strengthText.textContent = 'Fair';
        } else if (score > 0) {
            strengthBar.classList.add('weak');
            if (strengthText) strengthText.textContent = 'Weak';
        }
    }

    if (formGroup) {
        if (score === 4 && value.length > 0) {
            formGroup.classList.add('has-success');
            formGroup.classList.remove('has-error');
        } else if (value.length > 0) {
            formGroup.classList.add('has-error');
            formGroup.classList.remove('has-success');
        } else {
            formGroup.classList.remove('has-success', 'has-error');
        }
    }

    return score === 4;
}

function validatePasswordMatch(passwordInput, confirmPasswordInput) {
    if (!passwordInput || !confirmPasswordInput) return true;

    const matchHint = document.getElementById('passwordMatch');
    const formGroup = confirmPasswordInput.closest('.form-group');

    const passwordsMatch = passwordInput.value === confirmPasswordInput.value &&
                          confirmPasswordInput.value.length > 0;

    if (matchHint) {
        if (passwordsMatch) {
            matchHint.classList.add('valid');
            matchHint.classList.remove('invalid');
        } else {
            matchHint.classList.add('invalid');
            matchHint.classList.remove('valid');
        }
    }

    if (formGroup) {
        if (passwordsMatch && confirmPasswordInput.value.length > 0) {
            formGroup.classList.add('has-success');
            formGroup.classList.remove('has-error');
        } else if (confirmPasswordInput.value.length > 0) {
            formGroup.classList.add('has-error');
            formGroup.classList.remove('has-success');
        } else {
            formGroup.classList.remove('has-success', 'has-error');
        }
    }

    return passwordsMatch;
}

function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `alert alert-${type}`;
    notification.innerHTML = `
        <svg class="alert-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
        <span>${message}</span>
    `;

    const formContainer = document.querySelector('.auth-form-container');
    if (formContainer) {
        formContainer.insertBefore(notification, formContainer.firstChild);

        setTimeout(() => {
            notification.style.animation = 'slideOut 0.3s ease-out forwards';
            setTimeout(() => notification.remove(), 300);
        }, 5000);
    }
}

// Add slideOut animation
const style = document.createElement('style');
style.textContent = `
    @keyframes slideOut {
        from {
            opacity: 1;
            transform: translateY(0);
        }
        to {
            opacity: 0;
            transform: translateY(-10px);
        }
    }
`;
document.head.appendChild(style);