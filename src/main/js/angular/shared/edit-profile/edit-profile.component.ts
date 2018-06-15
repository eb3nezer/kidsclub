import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../services/app-title.service";
import { User } from "../model/user";
import { UserProfileService } from "../services/user-profile.service";
import {MatSnackBar} from "@angular/material";
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from "@angular/common";

@Component({
    selector: 'app-edit-profile',
    templateUrl: './edit-profile.component.html',
    styleUrls: ['./edit-profile.component.css']
})
export class EditProfileComponent implements OnInit {
    currentUser: User;
    fileToUpload: File;
    newFilename = "";
    newUser: boolean = false;

    constructor(
        private apptitleService: AppTitleService,
        private userProfileService: UserProfileService,
        private snackBar: MatSnackBar,
        private route: ActivatedRoute,
        private router: Router,
        private location: Location) {
        this.currentUser = new User();
    }

    onSubmit() {
        this.userProfileService.updateCurrentUserObservable(this.currentUser, this.fileToUpload).subscribe(next => {
            this.currentUser = next;
            this.newFilename = "";
            this.snackBar.open(`Profile updated`, 'Dismiss', {
                duration: 10000,
            });
            if (this.newUser) {
                this.router.navigate([""]);
            }
        });
    }

    onCancel() {
        if (this.newUser) {
            this.router.navigate([""]);
        } else {
            this.location.back();
        }
    }

    setFile(event) {
        if (event.srcElement.files && event.srcElement.files.length >= 1) {
            this.fileToUpload = event.srcElement.files[0];
            this.newFilename = this.fileToUpload.name;
        }
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.apptitleService.setTitle("Edit your profile"));
        const profileMagic: number = +this.route.snapshot.paramMap.get('id');
        if (profileMagic == 5) {
            this.newUser = true;
        }

        this.userProfileService.getCurrentUserObservable().subscribe(next => this.currentUser = next);
    }
}
