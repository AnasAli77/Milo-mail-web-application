import { Component } from '@angular/core';
import { Routes } from '@angular/router';
import { loginComponent } from './components/login/loginComponent';
import { SignUpComponent } from './components/sign-up-component/sign-up-component';

export const routes: Routes = [
    {path: 'Login', component: loginComponent},
    {path: 'Sign-Up', component: SignUpComponent}
];
