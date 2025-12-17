import { Safty } from './components/safty/safty';
import { FirstPage } from './components/first-page/first-page';
import { authGuard } from './guards/auth-guard';
import { Routes } from '@angular/router';
import { loginComponent } from './components/login/loginComponent';
import { SignUpComponent } from './components/sign-up-component/sign-up-component';
import { Layout } from './components/layout/layout';
import { EmailList } from './components/email-list/email-list';
import { Compose } from './components/compose/compose';
import { EmailViewComponent } from './components/email-viewer/email-viewer';
import { Contacts } from './components/contacts/contacts';
import { isNotLoginGuard } from './guards/is-not-login-guard';
import { FiltersComponent } from './components/filters/filters';
import { StatsComponent } from './components/stats/stats';


export const routes: Routes = [

  { path: '', component: FirstPage },
  { path: 'Login', component: loginComponent, canActivate: [isNotLoginGuard] },
  { path: 'Sign-Up', component: SignUpComponent, canActivate: [isNotLoginGuard] },
  {
    path: 'layout',
    component: Layout, canActivate: [authGuard],
    runGuardsAndResolvers: 'always',
    children: [
      { path: '', redirectTo: 'inbox', pathMatch: 'full' },
      { path: 'contacts', component: Contacts },
      { path: 'filters', component: FiltersComponent },
      { path: 'stats', component: StatsComponent },
      {
        path: ':folderId',
        component: EmailList,
        children: [
          { path: 'compose', component: Compose },
          { path: 'email/:id', component: EmailViewComponent }
        ]
      }
    ]
  },
  { path: '**', component: Safty }
];
