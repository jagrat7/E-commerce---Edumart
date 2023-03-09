import { Component, OnInit, Input } from '@angular/core';
import { ProductService } from '../product.service';
import { ActivatedRoute, Router } from '@angular/router';
import { SessionService } from '../session.service';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent implements OnInit {
  username: string = '';
  password: string = '';
  message: string = '';
  user_type: string = '';
  public userDetails: any;
  appComponent: typeof AppComponent;

  constructor(
    private productService: ProductService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private sessionService: SessionService
  ) {
    this.appComponent = AppComponent;
  }

  ngOnInit(): void {
    //does nothing on initialization
  }

  register() {
    this.message = '';
    if (this.username.trim() !== '' && this.password.trim() !== '') {
      this.productService.registerUser(this.username, this.password).subscribe({
        next: (userDetails) => {
          debugger;
          console.log(userDetails);
          this.userDetails = userDetails;
          if (
            userDetails.hasOwnProperty('registration') &&
            userDetails.registration == 'success'
          ) {
            this.user_type = 'customer';
            this.sessionService.logIn(
              userDetails.username,
              userDetails.id,
              this.user_type
            );
            this.router.navigate(['home']).then(() => {
              window.location.reload();
            });
          } else {
            this.sessionService.logInGuest();
            this.message = 'Username already exists';
          }
        },
      });
    }
  }
  cancel() {
    this.router.navigate(['home']).then(() => {
      window.location.reload();
    });
  }
}
