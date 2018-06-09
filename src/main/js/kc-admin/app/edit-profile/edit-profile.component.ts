import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../services/app-title.service";
import { User } from "../model/user";
import { UserProfileService } from "../services/user-profile.service";

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css']
})
export class EditProfileComponent implements OnInit {
  currentUser: User;

  constructor(
    private apptitleService: AppTitleService,
    private userProfileService: UserProfileService) {
    this.currentUser = new User("");
  }

  onSubmit() {
      console.log("Submitted");
      this.currentUser.email = "Hello@here";
  }

  ngOnInit() {
  }

  ngAfterViewInit() {
    Promise.resolve(null).then(() => this.apptitleService.setTitle("Edit your profile"));
    this.userProfileService.getCurrentUserObservable().subscribe(next => this.currentUser = next);
  }
}
