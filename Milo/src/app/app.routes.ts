import {Routes} from '@angular/router';
import {loginComponent} from './components/login/loginComponent';
import {SignUpComponent} from './components/sign-up-component/sign-up-component';
import {Layout} from './components/layout/layout';
import {EmailList} from './components/email-list/email-list';
import {Compose} from './components/compose/compose';


export const routes: Routes = [
  { path: 'login', component:  loginComponent },
  { path: 'signup', component: SignUpComponent },
  {
    path: '',
    component: Layout,
    children: [
      { path: '', redirectTo: 'inbox', pathMatch: 'full' },
      {
        path: ':folderId',
        component: EmailList,
        children: [
          { path: 'compose', component: Compose }
        ]
      }
    ]
  },
  { path: '**', redirectTo: 'login' }
];
