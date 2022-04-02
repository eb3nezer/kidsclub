import { Injectable } from '@angular/core';
import {of} from "rxjs/internal/observable/of";
import {Observable} from "rxjs/index";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmDialogComponent} from "../confirm-dialog/confirm-dialog.component";

@Injectable({
  providedIn: 'root'
})
export class HttpErrorService {

    constructor(private dialog: MatDialog) { }

    handleErrorWithDialog<T> (operation = 'operation', result?: T) {
        return (error: any): Observable<T> => {

            let message = "";
            if (error && error.status && (error.status == 412)) {
                message += "Validation failed. ";
            }
            if (error && error.error && error.error.error) {
                message += error.error.error;
            } else if (error && error.message) {
                message += error.message;
            }

            let dialogRef = this.dialog.open(ConfirmDialogComponent, {
                data: {
                    title: "Error",
                    text: `Operation ${operation} failed: ${message}`,
                    buttons: ["OK"]
                }
            });

            // Let the app keep running by returning an empty result.
            return of(result as T);
        };
    }

    handleErrorWithConsoleLog<T> (operation = 'operation', result?: T) {
        return (error: any): Observable<T> => {

            let message = "";
            if (error && error.status && (error.status == 412)) {
                message += "Validation failed. ";
            }
            if (error && error.error && error.error.error) {
                message += error.error.error;
            } else if (error && error.message) {
                message += error.message;
            }

            console.error(`Operation ${operation} failed: ${message}`);

            // Let the app keep running by returning an empty result.
            return of(result as T);
        };
    }
}
