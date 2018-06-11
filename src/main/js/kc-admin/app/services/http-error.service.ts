import { Injectable } from '@angular/core';
import {of} from "rxjs/internal/observable/of";
import {Observable} from "rxjs/index";
import {ConfirmDialogComponent} from "../confirm-dialog/confirm-dialog.component";
import {MatDialog} from "@angular/material";

@Injectable({
  providedIn: 'root'
})
export class HttpErrorService {

    constructor(private dialog: MatDialog) { }

    handleError<T> (operation = 'operation', result?: T) {
        return (error: any): Observable<T> => {

            let dialogRef = this.dialog.open(ConfirmDialogComponent, {
                //width: '250px',
                data: {
                    title: "Error",
                    text: `Operation ${operation} failed: ${error.message}`,
                    buttons: ["OK"]
                }
            });

            // Let the app keep running by returning an empty result.
            return of(result as T);
        };
    }
}
