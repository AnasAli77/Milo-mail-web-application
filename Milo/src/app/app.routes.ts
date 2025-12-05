import { Routes } from '@angular/router';
import { loginComponent } from './components/login/loginComponent';
import { SignUpComponent } from './components/sign-up-component/sign-up-component';
import { Layout } from './components/layout/layout';
import { EmailList } from './components/email-list/email-list';
import { Compose } from './components/compose/compose';
import { EmailViewComponent } from './components/email-viewer/email-viewer';
import { Safty } from './components/safty/safty';
import { FirstPage } from './components/first-page/first-page';
import { authGuard } from './guards/auth-guard';


export const routes: Routes = [
  {path: '', component: FirstPage},
  { path: 'Login', component: loginComponent },
  { path: 'Sign-Up', component: SignUpComponent },
  {
    path: 'layout',
    component: Layout, canActivate: [authGuard],
    runGuardsAndResolvers: 'always',
    children: [
      { path: '', redirectTo: 'inbox', pathMatch: 'full' },

      {
        // 1. EmailList is the PARENT component here because it holds the <router-outlet>
        path: ':folderId',
        component: EmailList,
        children: [
          // 2. These children load INSIDE the EmailList's router-outlet (Right Pane)

          // Matches: /inbox/compose
          { path: 'compose', component: Compose },

          // Matches: /inbox/email/16999
          { path: 'email/:id', component: EmailViewComponent }
        ]
      }
    ]
  },
  { path: '**', component: Safty }]