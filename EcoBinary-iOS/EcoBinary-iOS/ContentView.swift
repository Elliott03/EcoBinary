//
//  ContentView.swift
//  EcoBinary-iOS
//
//  Created by Elliott Phillips on 2/13/24.
//

import SwiftUI

struct ContentView: View {
    @State private var privateIp: String = ""
    @State private var port: String = ""

    var body: some View {
        VStack {
            HStack {
                VStack {
                    Text("Private IP:")
                    TextField("",text: $privateIp)
                        .background(Color("InputBackgroundColor"))
                }
                Text("EcoBinary")
                VStack{
                    Text("Port:")
                    TextField("",text: $privateIp)
                        .background(Color("InputBackgroundColor"))
                }
            }
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
